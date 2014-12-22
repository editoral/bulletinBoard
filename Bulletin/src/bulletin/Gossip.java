package bulletin;

import java.io.Serializable;

public class Gossip implements Serializable {
	public TimeStamp ts;
	public Log log;
	@Override
	public String toString() {
		return "TS: " + ts.toString() + " Log: " +  log.toString();
	}
}
