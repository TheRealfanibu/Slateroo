package items.manage;

public class TimeEffect {
	private Class<?> itemClass;
	private long effectTime; // in milliseconds
	private long startTime;
	
	public TimeEffect(long effectTime, Class<?> itemClass) {
		startTime = System.currentTimeMillis();
		this.effectTime = effectTime;
		this.itemClass = itemClass;
	}

	public Class<?> getItemClass() {
		return itemClass;
	}
	
	public double calcFinishInput() {
		return (double) (System.currentTimeMillis() - startTime) / effectTime;
	}
}
