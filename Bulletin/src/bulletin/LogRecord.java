package bulletin;

import java.io.Serializable;

public class LogRecord implements Comparable, Serializable {
	public int rm;
	public TimeStamp ts;
	public Message op;
	public TimeStamp prev;
	public int cid;
	
	public LogRecord(int rmId, TimeStamp ts, Update u) {
		rm = rmId;
		this.ts = ts;
		op = u.op;
		prev = u.prev;
		cid = u.cid;
	}
	
	
	@Override
	public int compareTo(Object obj) {
		LogRecord in = (LogRecord) obj;
		return ts.compareTo(in.ts);
	}
	
	@Override
	public boolean equals(Object obj) {
		LogRecord in = (LogRecord) obj;
		boolean result = false;
		if (ts.compareTo(in) == 0) {
			result = true;
		}
		return result;
	}
	
	@Override
	public String toString() {
		String result = new String("<RM: " + rm + "; TS: " + ts + "; Msg: " + op.title + "; Msg.TS: " + prev + "; ID: " + cid);
		return result;
	}
	
}
