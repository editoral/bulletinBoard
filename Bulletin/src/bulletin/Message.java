package bulletin;

import java.io.Serializable;

public class Message implements Serializable {
	public String title;
	
	@Override
	public String toString() {
		return title;
	}
}
