package bulletin;

import java.util.ArrayList;

import mpi.*;

public class RM {
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
	
	
	public RM(int id, int[] RMs) {
		System.out.println(rank + " Im a RM and got intantiated");
		this.id = id;
		executeGossipTime = System.currentTimeMillis() + executeGossipTime;
		rms = RMs;
		replicaTS = new TimeStamp();
		valueTS = new TimeStamp();
		updateLog = new Log();
		queue = new PendingQueryQueue();
	}
	
	public void listenerLoop() {
		Object[] buffer = new Object[2];
		Request rec = MPI.COMM_WORLD.Irecv(buffer, 0, 2, MPI.OBJECT, MPI.ANY_SOURCE, 0);
		Status stat = new Status();
		while(rec.Test() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stat = rec.Test();
		}
		dispatch(buffer, stat);
		if(executeGossipTime < System.currentTimeMillis()) {
			System.out.println(rank + "It is Time for Gossip!");
			sendGossip();
		}
		listenerLoop();
	}
	
	private void dispatch(Object[] buffer, Status stat) {
		MessageType type = (MessageType) buffer[0];
		switch (type) {
			case UPDATE:handleUpdate(buffer[1], stat);
						break;
			case QUERY: handleQuery(buffer[1], stat);
						break;
			case GOSSIP: handleGossip(buffer[1]);
						 break;
		}
	}
	
	private void handleUpdate(Object obj, Status stat) {
		System.out.println(rank + "Got an Update from " + stat.source);
		Update u = (Update) obj;
		System.out.println(rank + " DEBUG " + u.op.title);
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
		System.out.println(rank + "I sended an UpdateResponse to " + stat.source);
		if (u.prev.isAbsoluteSmallerOrEqual(valueTS)) {
			execute(u);	
		}
	}
	
	private void handleQuery(Object obj, Status stat) {
		System.out.println(rank + "Got a Query form " + stat.source);
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
		for (int i = 0; i < rms.length; i++) {
			Gossip g = new Gossip();
			g.log = updateLog;
			g.ts = replicaTS;
			int target = rms[i];
			Object[] buffer = new Object[2];
			buffer[0] = MessageType.GOSSIP;
			buffer[1] = g;
			System.out.println(rank + "I'am going to send a Gossip to " + target);
			MPI.COMM_WORLD.Send(buffer, 0, 2, MPI.OBJECT, target, 0);
		}
	}
	
	private void handleGossip(Object obj) {
		System.out.println(rank + "Got a Gossip");
		Gossip g = (Gossip) obj;
		updateLog.insertLog(g.log);
		replicaTS = replicaTS.max(g.ts);
		
	}

	private void execute(Update u) {
		value.add(u.op);
		valueTS = valueTS.max(u.prev);
		executed.insert(u.prev);
	}
	
	private void sendQueryResponse(int target) {
		System.out.println(rank + "I'am ready to send an UpdateResponse to " + target);
		Object[] buffer = new Object[2];
		MessageType type = MessageType.QUERY;
		Query q = new Query();
		q.value = value;
		q.valueTS = valueTS;
		buffer[0] = type;
		buffer[1] = q;
		MPI.COMM_WORLD.Send(buffer, 0, 2, MPI.OBJECT, target, 0);		
		System.out.println(rank + "I sended an UpdateResponse to " + target);
	}
	
	
}
