package game;

public class FPSCounter {
	private long startTime;
	private int frames;
	
	private final String name;
	private final int outputTime;
	
	private int outputCounter = 0;
	private int frameSum;
	
	public FPSCounter(String name, int outputTime) {
		if(outputTime < 1)
			throw new IllegalArgumentException("outputTime must be greater than one in FPSCounter");
		
		this.name = name;
		this.outputTime = outputTime;
	}
	
	public FPSCounter(){
		this(null, 1);
	}
	
	public FPSCounter(String name) {
		this(name, 1);
	}
	
	
	public FPSCounter(int outputTime) {
		this(null, outputTime);
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public void count() {
		long curTime = System.currentTimeMillis();
		long delta = curTime - startTime;
		if (delta >= 1000) {
			outputCounter++;
			frameSum += frames;
			if(outputCounter == outputTime) {
				int averageFrames = frameSum / outputTime;
				String output;
				if(name != null)
					output = name + ": " + averageFrames;
				else
					output = String.valueOf(averageFrames);
				
				if(outputTime != 1)
					output = "Average of " + outputTime + ": " + output;
				
				System.out.println(output);
					
				outputCounter = 0;
				frameSum = 0;
			}
			frames = 1;
			start();
		}
		else 
			frames++;
	}
}
