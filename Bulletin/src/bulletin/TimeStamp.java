package bulletin;

public class TimeStamp implements Comparable {
	private static int size = 0;
	private int[] vektor;
	public TimeStamp() {
		if (size == 0) {
			System.out.println("Vektor Size has not been initialized");
		}
		vektor = new int[size];
	}
	
	public static void setSize(int i) {
		size = i;
	}
	
	public void setValAtIndex(int index, int in) {
		vektor[index] = in;
	}
	
	public void incrementAtIndex(int index) {
		vektor[index]++;
	}
	
	public int getValAtIndex(int i) {
		return vektor[i];
	}
	
	public int[] getVektor() {
		return vektor;
	}

	@Override
	public int compareTo(Object obj) {
		TimeStamp in = (TimeStamp) obj;
		int[] inVektor = in.getVektor();
		int result = 0;
		for(int i = 0; i < size; i++) {
			if (this.vektor[i] < inVektor[i]) {
				result = -1;
				break;
			} else if (this.vektor[i] > inVektor[i]) {
				result = 1;
				break;
			}
		}
		in.getVektor();
		return result;
	}
	
	public boolean isAbsoluteSmallerOrEqual(TimeStamp ts) {
		boolean result = true;
		for(int i = 0; i < size; i++) {
			if (vektor[i] < ts.getValAtIndex(i)) {
				result = false;
			}
		}
		return result;
	}
	
	public TimeStamp max(TimeStamp ts) {
		TimeStamp result = new TimeStamp();
		for(int i = 0; i < size; i++) {
			if (vektor[i] < ts.getValAtIndex(i)) {
				result.setValAtIndex(i, ts.getValAtIndex(i));
			} else {
				result.setValAtIndex(i, vektor[i]);
			}
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		TimeStamp ts = (TimeStamp) obj;
		boolean result = false;
		for(int i = 0; i < size; i++) {
			if (vektor[i] != ts.getValAtIndex(i)) {
				result = false;
				break;
			}
		}
		return result;
	}
}
