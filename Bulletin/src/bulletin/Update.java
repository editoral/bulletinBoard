package bulletin;

import java.io.Serializable;

public class Update implements Serializable {
	public int cid;
	public Message op;
	public TimeStamp prev;
}
