package bulletin;

import java.io.Serializable;

public class LogRecord implements Serializable {
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
	
	
}
