package bulletin;

import java.util.Random;

import mpi.MPI;

public class FE {
	private int id;
	private Random rand;
	private int rank = MPI.COMM_WORLD.Rank();
	private Log mylog;
	private TimeStamp prev;
	private int[] rms;
	
	public FE(int id, int[] RMs) {
		this.id = id;
		this.rms = RMs;
		rand = new Random();
	}
	
	public void start() {
		System.out.println(rank + " Im a FE and got started");
		long nextSend = System.currentTimeMillis() + rand.nextInt(1000) + 500;
		while(true) {
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			if (nextSend < System.currentTimeMillis()) {
				chooseAction();
				nextSend = System.currentTimeMillis() + rand.nextInt(1000) + 500;
			}
		}
	}
	
	private void chooseAction() {
		switch (rand.nextInt(3)) {
			case 0 :
				sendUpdate();
				break;
			case 1 :
				sendQuery();
				break;
			case 2 :
				sendResponse();
				break;
		}
	}
	
	private void sendUpdate() {
		System.out.println(rank + " Im a FE and I choose to make an Update");
		Message m = new Message();
		MessageType type = MessageType.UPDATE;
		m.title = Integer.toString(rand.nextInt(10000));
		
		int randomNr = rand.nextInt(rms.length);
		int targetRM = rms[randomNr];
		Object[] buffer = new Object[2];
		Update u = new Update();
		u.cid = id; 
		u.op = m;
		u.prev = prev;
		
		buffer[0] = type;
		buffer[1] = u;
		MPI.COMM_WORLD.Send(buffer, 0, 1, MPI.OBJECT, targetRM, 0);
		System.out.println(rank + " Im a FE and I sended an Update now waiting");
		Object[] buffer2 = new Object[2];
		MPI.COMM_WORLD.Recv(buffer2, 0, 2, MPI.OBJECT, targetRM, 0);
		System.out.println(rank + " Im a FE and i got an Update Response");
		Update respU = (Update) buffer2[1];
		prev = prev.max(respU.prev);
	}
	
	private void sendResponse() {
		System.out.println(rank + " Im a FE and I choose to make a Response");
	}
	
	private void sendQuery() {
		System.out.println(rank + " Im a FE and I choose to make a Query");
		MessageType type = MessageType.QUERY;
	
		int randomNr = rand.nextInt(rms.length);
		int targetRM = rms[randomNr];
		Object[] buffer = new Object[2];

		Query q = new Query();
		q.prev = prev;		
		
		buffer[0] = type;
		buffer[1] = q;
		MPI.COMM_WORLD.Send(buffer, 0, 2, MPI.OBJECT, targetRM, 0);	
		System.out.println(rank + " Im a FE and i sended an Query to " + targetRM);	
		Object[] bufferR = new Object[2];
		MPI.COMM_WORLD.Recv(bufferR, 0, 2, MPI.OBJECT, targetRM, 0);
		System.out.println(rank + " Im a FE and i got an Query Response from " + targetRM);
	}
}
