package bulletin;

import java.io.Serializable;
import java.util.ArrayList;

import mpi.*;

public class RM implements Serializable{
	public static long minGossipInterval = 1000;
	private int rank = MPI.COMM_WORLD.Rank();
	private int id;
	private TimeStamp replicaTS;
	private Log updateLog;
	private TimeStamp valueTS;
	private ArrayList<Message> value;
	private Executed executed;
	private PendingQueryQueue queue;
	private long executeGossipTime;
	private int[] rms;
	private Request[] pendingGossips;
	private boolean isGossipPending;
	private boolean terminated;
	private long timeUntilShutdown;
	
	
	public RM(int id, int[] RMs) {
		System.out.println(rank + " Im a RM and got intantiated");
		this.id = id;
		executeGossipTime = System.currentTimeMillis() + executeGossipTime;
		rms = RMs;
		replicaTS = new TimeStamp();
		valueTS = new TimeStamp();
		updateLog = new Log();
		queue = new PendingQueryQueue();
		value = new ArrayList<Message>();
		executed = new Executed();
		pendingGossips =  new Request[rms.length];
	}
	
	public void listenerLoop() {
		Object[] buffer = new Object[2];
		Request rec = MPI.COMM_WORLD.Irecv(buffer, 0, 2, MPI.OBJECT, MPI.ANY_SOURCE, 0);
		Status stat = null;
		boolean testLoop = true;
		while(testLoop) {
			System.out.println(rank + " DEBUG I'm Testing for incoming!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stat = rec.Test(); 
			if (stat != null) {
				testLoop = false;
			}
			if (terminated && timeUntilShutdown < System.currentTimeMillis()) {
				testLoop = false;
			}
		}

		if (terminated && (timeUntilShutdown < System.currentTimeMillis())) {
			terminate();
		} else {
			dispatch(buffer, stat);
			gossipHandler();
			listenerLoop();
		}	
	}
	
	private void gossipHandler() {
		if (isGossipPending) {
			int accumulator = 0;
			for(int i = 0; i < rms.length; i++) {
				if (rms[i] != rank) {
					if (pendingGossips[i].Test() != null) {		
							accumulator++;			
					}
				}					
			}
			if (accumulator == (rms.length - 1)) {
				isGossipPending = false;
			}			
		} else {
			if(executeGossipTime < System.currentTimeMillis()) {
				System.out.println(rank + " It is Time for Gossip!");
				sendGossip();
			}
		}		
	}
	
	private void dispatch(Object[] buffer, Status stat) {
		MessageType type = (MessageType) buffer[0];
		//System.out.println(rank + " DEBUG " + type);
		//System.out.println(rank + " DEBUG " + buffer[1]);
		switch (type) {
			case UPDATE:handleUpdate(buffer[1], stat);
						break;
			case QUERY: handleQuery(buffer[1], stat);
						break;
			case GOSSIP: handleGossip(buffer[1]);
						 break;
			case TERMINATE: handleTermination();
							break;
		}
	}
	
	private void handleUpdate(Object obj, Status stat) {
		System.out.println(rank + " Got an Update from " + stat.source);
		Update u = (Update) obj;
		replicaTS.incrementAtIndex(id);
		TimeStamp ts = u.prev;
		ts.setValAtIndex(id, replicaTS.getValAtIndex(id));
		updateLog.createEntry(id, ts, u);
		Object[] buffer = new Object[2];
		MessageType type = MessageType.UPDATE;
		Update respU = new Update();
		respU.prev = ts;
		buffer[0] = type;
		buffer[1] = respU;
		MPI.COMM_WORLD.Send(buffer, 0, 2, MPI.OBJECT, stat.source, 0);
		System.out.println(rank + " I sended an UpdateResponse to " + stat.source);
		if (u.prev.isAbsoluteSmallerOrEqual(valueTS)) {
			this.execute(u);	
		}
	}
	
	private void handleQuery(Object obj, Status stat) {
		System.out.println(rank + " Got a Query form " + stat.source);
		Query q = (Query) obj;
		if (q.prev.isAbsoluteSmallerOrEqual(valueTS)) {
			sendQueryResponse(stat.source);
		} else {
			PendingQueryQueue que = new PendingQueryQueue();
			que.respondTo = stat.source;
			que.ts = q.prev;
		}
	}
	
	private void sendGossip() {
		pendingGossips = new Request[rms.length];
		for (int i = 0; i < rms.length; i++) {
			Gossip g = new Gossip();
			g.log = updateLog;
			g.ts = replicaTS;
			int target = rms[i];
			Object[] buffer = new Object[2];
			buffer[0] = MessageType.GOSSIP;
			buffer[1] = g;		
			if (target != rank) {
				System.out.println(rank + " I'am going to send a Gossip to " + target);
				pendingGossips[i] = MPI.COMM_WORLD.Isend(buffer, 0, 2, MPI.OBJECT, target, 0);
				isGossipPending = true;
			}
			
		}
	}
	
	private void handleGossip(Object obj) {
		System.out.println(rank + " Got a Gossip");
		Gossip g = (Gossip) obj;
		updateLog.insertLog(g.log);
		replicaTS = replicaTS.max(g.ts);
		System.out.println(this);
	}

	private void execute(Update u) {
		value.add(u.op);
		valueTS = valueTS.max(u.prev);
		executed.insert(u.prev);
	}
	
	private void sendQueryResponse(int target) {
		System.out.println(rank + " I'am ready to send a QueryResponse to " + target);
		Object[] buffer = new Object[2];
		MessageType type = MessageType.QUERY;
		Query q = new Query();
		q.value = value;
		q.valueTS = valueTS;
		buffer[0] = type;
		buffer[1] = q;
		System.out.println(rank + " DEBUG ResponseTS " + q.valueTS);
		MPI.COMM_WORLD.Send(buffer, 0, 2, MPI.OBJECT, target, 0);		
		System.out.println(rank + " I sended an QueryResponse to " + target);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		sb.append(	"**************************************" + newLine);
		sb.append(	"TYPE: RM" + newLine);
		sb.append(	"RANK: " + rank + newLine);
		sb.append(	"ID: " + id + newLine);
		sb.append(	"Replica Timestamp: " + replicaTS + newLine);
		sb.append(updateLog);
		sb.append(	"Value Timestamp: "+ valueTS + newLine);		
		sb.append(	"Value: " + newLine);
		sb.append("----------Start----------" + newLine);
		for (Message m : value) {
			sb.append(m.title + newLine);
		}
		sb.append("-----------End-----------" + newLine);
		sb.append(	"**************************************"	);
		return sb.toString();
	}
	
	private void handleTermination() {
		if (!terminated) {
			terminated = true;
			timeUntilShutdown = System.currentTimeMillis() + 4000;
		}		
	}
	
	private void terminate() {
		FileHandler.write("RM" + rank, this.toString());
		System.out.println(rank + " Im an RM ++++++++++I TERMINATE! ++++++++");
	}
}
