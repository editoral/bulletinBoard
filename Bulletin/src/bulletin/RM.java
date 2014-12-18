package bulletin;

import java.util.ArrayList;

import mpi.*;

public class RM {

	private int rank = MPI.COMM_WORLD.Rank();
	private int id;
	private TimeStamp replicaTS;
	private Log updateLog;
	private TimeStamp valueTS;
	private ArrayList<Message> value;
	private Executed executed;
	
	public RM(int id) {
		this.id = id;
	}
	
	public void listenerLoop() {
		Object[] buffer = new Object[2];
		Request rec = MPI.COMM_WORLD.Irecv(buffer, 0, 2, MPI.OBJECT, MPI.ANY_SOURCE, 0);
		while(rec.Test() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}					
		}
		dispatch(buffer);
		listenerLoop();
	}
	
	private void dispatch(Object[] buffer) {
		MessageType type = (MessageType) buffer[0];
		switch (type) {
			case UPDATE:handleUpdate(buffer[1]);
						break;
			case QUERY: handleQuery(buffer[1]);
						break;
			case GOSSIP: handleGossip(buffer[1]);
						 break;
		}
	}
	
	private void handleUpdate(Object obj) {
		System.out.println(rank + "Got an Update");
		Update u = (Update) obj;
		replicaTS.incrementAtIndex(id);
		TimeStamp ts = u.prev;
		ts.setValAtIndex(id, replicaTS.getValAtIndex(id));
		updateLog.createEntry(id, ts, u);
		if (u.prev.isAbsoluteSmallerOrEqual(valueTS)) {
			execute(u);	
		}
	}
	
	private void handleQuery(Object obj) {
		System.out.println(rank + "Got a Query");
		Query q = (Query) obj;
	}
	
	private void handleGossip(Object obj) {
		System.out.println(rank + "Got a Gossip");
		Gossip p = (Gossip) obj;
	}

	private void execute(Update u) {
		value.add(u.op);
		valueTS = valueTS.max(u.prev);
		executed.insert(u.prev);
	}
	
	
}
