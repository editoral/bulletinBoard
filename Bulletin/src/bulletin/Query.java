package bulletin;

import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable {
	public TimeStamp prev;
	public ArrayList<Message> value;
	public TimeStamp valueTS;
}
