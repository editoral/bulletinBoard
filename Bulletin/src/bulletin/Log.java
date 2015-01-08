package bulletin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;

public class Log implements Serializable {
	private ArrayList<LogRecord> recs;
	private TreeSet<LogRecord> recsTree;
	private Random rand;
	
	public Log() {
		rand = new Random();
		recs = new ArrayList<LogRecord>();
		recsTree = new TreeSet<LogRecord>();
	}

	public LogRecord createEntry(int rm, TimeStamp ts, Update u) {
		LogRecord logRec = new LogRecord(rm, ts, u);
		recs.add(logRec);
		recsTree.add(logRec);
		return logRec;
	}
	
	public void insertLog(Log log) {
		Iterator<LogRecord> iter = log.recsTree.iterator();
		while(iter.hasNext()) {
			LogRecord logRec = iter.next();
			recsTree.add(logRec);
		}
	}
	
	public ArrayList<LogRecord> logRecsToExecute(TimeStamp ts) {
		LogRecord tester = new LogRecord(0, ts ,new Update());
		ArrayList<LogRecord> result = new ArrayList<LogRecord>();
		NavigableSet<LogRecord> navSet = recsTree.headSet(tester, true);
		Iterator<LogRecord> iter = navSet.iterator();
		while(iter.hasNext()) {
			LogRecord logRec = iter.next();
			result.add(logRec);
		}
		return result;
	}
	
//	public LogRecord getRandomRecord() {
//		int index = rand.nextInt(recs.size());
//		return recs.get(index);
//	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		sb.append("LOG: " + newLine);
		sb.append("----------Start----------" + newLine);
//		for (LogRecord rec : recs) {
//			sb.append(rec + newLine);
//		}
		Iterator<LogRecord> iter = recsTree.iterator();
		while(iter.hasNext()) {
			LogRecord logRec = iter.next();
			sb.append(logRec + newLine);
		}		
		sb.append("-----------End-----------" + newLine);
		return sb.toString();
	}
}
