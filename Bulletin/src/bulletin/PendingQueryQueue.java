package bulletin;

import java.io.Serializable;

public class PendingQueryQueue implements Serializable {
	public TimeStamp ts;
	public int respondTo; 
}
