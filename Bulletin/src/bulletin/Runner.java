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

		MPI.Init(args);
		
		boolean writeToFile = false;
		
		FileHandler.outputLocation = "C:\\Users\\Marc\\Desktop\\mpjTemp";
		FileHandler.init();
		if (writeToFile) {
			try {
				File f = new File(FileHandler.dir.toPath() + "\\log.txt");
				PrintStream ps = new PrintStream(f);
				System.setOut(ps);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

	
		System.out.println("Booting " + MPI.COMM_WORLD.Rank());
		TimeStamp.setSize(2);
		RM.minGossipInterval = 10000;
		FE.runTime  = 30;
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
		System.out.println(rank + " Programm finished");
		
		MPI.Finalize();
	}

}
