package bulletin;

import mpi.MPI;

public class Runner {

	public static void main(String[] args) {
		//TimeStamp.setSize(3);
		//System.out.println("halloWelt");
		RM rm;
		FE fe;
		int rank = MPI.COMM_WORLD.Rank();
		int[] rms = {1, 2};
		switch (rank) {
			case 1 : 
				rm = new RM(1);
				rm.listenerLoop();
				break;
			case 2 :
				rm = new RM(2);
				rm.listenerLoop();
				break;
			case 3 :
				fe = new FE(1, rms);
				fe.start();
				break;
			case 4 :
				fe = new FE(2, rms);
				fe.start();
				
		}
		
	}

}
