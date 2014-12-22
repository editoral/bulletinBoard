package bulletin;

import java.io.Serializable;

public class Update implements Serializable {
	public int cid;
	public Message op;
	public TimeStamp prev;
	
	@Override
	public String toString() {
		return "TITLE: " + op.toString() + " PREV: " + prev.toString();
	}
}
