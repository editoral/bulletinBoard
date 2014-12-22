package bulletin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import mpi.MPI;


public class FileHandler {

	public static String outputLocation;
	public static File dir;
	
	public static void init() {
		dir = new File(outputLocation);
		System.out.println(dir.getAbsolutePath());
		if(MPI.COMM_WORLD.Rank() == 0) {
			 if (!dir.exists()) {
				    try{
				        dir.mkdir();
				     } catch(SecurityException se){
				        //handle it
				     }        
				 }
				 for(File file: dir.listFiles()) {
					 file.delete();
				 }			
		}


	}
	
	public static void write(String fileName, String content) {
		File file = new File(dir.toPath() + "\\" + fileName + ".txt");
		try {
			file.createNewFile();
			PrintWriter out = new PrintWriter(file);
			out.print(content);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(file.getAbsolutePath() + " Im Going to Write to File");
		
	}
	
}
