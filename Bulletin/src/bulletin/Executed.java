package bulletin;

import java.io.Serializable;
import java.util.ArrayList;

public class Executed implements Serializable {
	private ArrayList<TimeStamp> stamps;
	
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
}
