package bulletin;

import java.io.Serializable;

public class PendingQuery implements Serializable {
	public TimeStamp ts;
	public int respondTo; 
}
