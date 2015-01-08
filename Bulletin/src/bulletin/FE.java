package bulletin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import mpi.MPI;
import mpi.Request;

public class FE implements Serializable {
	public static long runTime = 10;
	private long terminateAt;
	private int id;
	private Random rand;
	private int rank = MPI.COMM_WORLD.Rank();
	private Log mylog;
	private TimeStamp prev;
	private int[] rms;
	private ArrayList<Message> value;
	
	public FE(int id, int[] RMs) {
		this.id = id;
		this.rms = RMs;
		rand = new Random();
		prev = new TimeStamp();
		value = new ArrayList<Message>();
		terminateAt = System.currentTimeMillis() + (runTime * 1000);
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
			if (terminateAt < System.currentTimeMillis()) {
				sendTermination();
				FileHandler.write("FE" + rank, this.toString());
				System.out.println(rank + " Im an FE ++++++++++I TERMINATE! ++++++++");
				break;
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
		executeUpdateSend(Integer.toString(rand.nextInt(10000)));
	}
	
	private void sendResponse() {
		System.out.println(rank + " Im a FE and I choose to make a Response");
		if (value.size() > 0) {
			int index = rand.nextInt(value.size());
			String title = "Re: " + value.get(index).title;
			executeUpdateSend(title);			
		}
	}
	
	private void executeUpdateSend(String title) {
		Message m = new Message();
		MessageType type = MessageType.UPDATE;
		m.title = title;
		
		int randomNr = rand.nextInt(rms.length);
		int targetRM = rms[randomNr];
		Object[] buffer = new Object[2];
		Update u = new Update();
		u.cid = id; 
		u.op = m;
		u.prev = prev;
		
		buffer[0] = type;
		buffer[1] = u;
		//System.out.println(rank + " DEBUG Im a FE and I'm going to send an Update to " + targetRM);
		MPI.COMM_WORLD.Send(buffer, 0, 2, MPI.OBJECT, targetRM, 0);
		System.out.println(rank + " Im a FE and I sended an Update to: " + targetRM);
		Object[] buffer2 = new Object[2];
		MPI.COMM_WORLD.Recv(buffer2, 0, 2, MPI.OBJECT, targetRM, 0);
		System.out.println(rank + " Im a FE and i got an Update Response from: " + targetRM);
		Update respU = (Update) buffer2[1];
		prev = prev.max(respU.prev);
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
		//System.out.println(rank + " DEBUG Im a FE and I'm going to send a Query to " + targetRM);
		MPI.COMM_WORLD.Send(buffer, 0, 2, MPI.OBJECT, targetRM, 0);	
		System.out.println(rank + " Im a FE and i sended an Query to " + targetRM);	
		Object[] bufferR = new Object[2];
		MPI.COMM_WORLD.Recv(bufferR, 0, 2, MPI.OBJECT, targetRM, 0);
		System.out.println(rank + " Im a FE and i got an Query Response from " + targetRM);
		
		Query answer = (Query) bufferR[1];
		value = answer.value;
		prev = prev.max(answer.valueTS);
		System.out.println(this);
	}
	
	private void sendTermination() {
		for (int i = 0; i < rms.length; i++) {
			int target = rms[i];
			Object[] buffer = new Object[2];
			buffer[0] = MessageType.TERMINATE;	
			System.out.println(rank + " Im a FE and im going to send a Termination to " + target);
			MPI.COMM_WORLD.Isend(buffer, 0, 2, MPI.OBJECT, target, 0);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		sb.append(	"**************************************" + newLine);
		sb.append(	"TYPE: FE" + newLine);
		sb.append(	"RANK: " + rank + newLine);
		sb.append(	"ID: " + id + newLine);
		sb.append(	"Prev Timestamp: " + prev + newLine);		
		sb.append(	"Value: " + newLine);
		sb.append("----------Start----------" + newLine);
		for (Message m : value) {
			sb.append(m.title + newLine);
		}
		sb.append("-----------End-----------" + newLine);
		sb.append(	"**************************************"	);
		return sb.toString();
	}	
}
