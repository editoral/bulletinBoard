package bulletin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Executed implements Serializable {
	private ArrayList<TimeStamp> stamps;
	
	public Executed() {
		stamps = new ArrayList<TimeStamp>();
	}
	
	public void insert(TimeStamp ts) {
		stamps.add(ts);
	}
	
	public boolean contains(TimeStamp ts) {
		boolean result = false;
		for(TimeStamp stamp : stamps) {
			if(stamp.equals(ts)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		sb.append("Executed: " + newLine);
		sb.append("----------Start----------" + newLine);
		for (TimeStamp stamp : stamps) {
			sb.append(stamp + newLine);
		}
		sb.append("-----------End-----------" + newLine);
		return sb.toString();
	}	
	
}
