package bulletin;

import java.io.Serializable;

public enum MessageType implements Serializable {
	UPDATE, QUERY, GOSSIP, TERMINATE
}
