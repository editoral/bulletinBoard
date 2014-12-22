package bulletin;

import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable {
	public TimeStamp prev;
	public ArrayList<Message> value;
	public TimeStamp valueTS;
	
	public Query() {
		prev = new TimeStamp();
		valueTS = new TimeStamp();
		value = new ArrayList<Message>();
	}
	
	@Override
	public String toString() {
		return "PREV: " + prev.toString() + " VALUE TS: " + valueTS.toString();
	}
}
