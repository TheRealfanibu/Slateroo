package utilities;

public class StopWatch {
	private long startTime;
	private String mode;
	
	private long getTime(String mode) {
		if(mode.equals("ms"))
			return System.currentTimeMillis();
		if(mode.equals("ns"))
			return System.nanoTime();
		else if(mode.equals("s"))
			return System.currentTimeMillis() / 1000;
		throw new RuntimeException("Wrong Time Mode in StopWatch class");
	}
	
	public void start() {
		start("ms");
	}
	
	public void start(String mode) {
		startTime = getTime(mode);
		
		this.mode = mode;
	}
	
	public int stop() {
		return (int) (getTime(this.mode) - startTime);
	}
	
	public void stopAndPrint(String text) {
		System.out.println(text + " " + stop() + mode);
	}
	
	public void stopAndPrint() {
		stopAndPrint("");
	}
}
