package bulletin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.io.output.TeeOutputStream;

import mpi.MPI;

public class Runner {

	public static void main(String[] args) {
//		File f = new File("C:\\Users\\Marc\\git\\bulletinBoard\\Bulletin\\src\\bulletin\\FileOut.txt");
//		PrintWriter writer;
//		try {
//			writer = new PrintWriter(f);
//			writer.print("");
//			writer.close();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//		    FileOutputStream fos = new FileOutputStream(f);
//		    //we will want to print in standard "System.out" and in "file"
//		    TeeOutputStream myOut=new TeeOutputStream(System.out, fos);
//		    PrintStream ps = new PrintStream(myOut);
//		    System.setOut(ps);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
		
		MPI.Init(args);
		
		System.out.println("Booting " + MPI.COMM_WORLD.Rank());
		TimeStamp.setSize(2);
		RM.minGossipInterval = 5000;
		RM rm;
		FE fe;
		int rank = MPI.COMM_WORLD.Rank();
		int[] rms = {0, 1};
		switch (rank) {
			case 0 : 
				rm = new RM(0, rms);
				rm.listenerLoop();
				break;
			case 1 :
				rm = new RM(1, rms);
				rm.listenerLoop();
				break;
			case 2 :
				fe = new FE(0, rms);
				fe.start();
				break;
			case 3 :
				fe = new FE(1, rms);
				fe.start();
				
		}
		
		
		MPI.Finalize();
	}

}
