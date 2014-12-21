package bulletin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Log implements Serializable {
	private ArrayList<LogRecord> recs;
	private Random rand;
	
	public Log() {
		rand = new Random();
		recs = new ArrayList<LogRecord>();
	}

	public LogRecord createEntry(int rm, TimeStamp ts, Update u) {
		LogRecord logRec = new LogRecord(rm, ts, u);
		recs.add(logRec);
		return logRec;
	}
	
	public void insertLog(Log log) {
		
	}
	
	public LogRecord getRandomRecord() {
		int index = rand.nextInt(recs.size());
		return recs.get(index);
	}
}
