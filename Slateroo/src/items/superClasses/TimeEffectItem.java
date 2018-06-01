package items.superClasses;

import java.util.Timer;
import java.util.TimerTask;

import items.manage.TimeEffect;
import items.markerInterfaces.ITimeEffectItem;
import logic.Snake;

public abstract class TimeEffectItem extends Item implements ITimeEffectItem{
	private final long effectTime;
	
	public TimeEffectItem(int x, int y, String iconName, double effectTime) {
		super(x, y, iconName);
		
		this.effectTime = (long) (effectTime * 1000);
	}
	
	protected abstract void effect(Snake snake);
	
	protected abstract void resetEffect(Snake snake);

	@Override
	protected void intersectionHandling(Snake snake) {
		effect(snake);
		TimeEffect effect = new TimeEffect(effectTime, getClass());
		snakeManager.getSnakes().forEach(snk -> snk.addTimeEffect(effect));
		TimerTask task = new TimerTask() {
			public void run() {
				resetEffect(snake);
				snakeManager.getSnakes().forEach(snk -> snk.removeTimeEffect(effect));
			}
		};
		new Timer().schedule(task, effectTime);
	}
}
