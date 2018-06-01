package items.superClasses;

import java.util.Timer;
import java.util.TimerTask;

import items.manage.ItemEffectMode;
import items.manage.TimeEffect;
import items.markerInterfaces.ITimeEffectItem;
import logic.Snake;

public abstract class MultiplePlayerTimeEffectItem extends MultiplePlayerEffectItem implements ITimeEffectItem{
	
	private final long effectTime;
	
	public MultiplePlayerTimeEffectItem(int x, int y, ItemEffectMode mode, String iconName, double effectTime) {
		super(x, y, mode, iconName);
		
		this.effectTime = (long) (effectTime * 1000);
	}
	
	protected abstract void resetEffect(Snake snake);
	
	@Override
	protected void intersectionHandling(Snake snake) {
		super.intersectionHandling(snake);
		TimeEffect effect = new TimeEffect(effectTime, getClass());
		executeOnSnakeDependingOnMode(snake, snk -> snk.addTimeEffect(effect));
		TimerTask task = new TimerTask() {
			public void run() {
				executeOnSnakeDependingOnMode(snake, snk ->  {
					resetEffect(snk);
					snk.removeTimeEffect(effect);
				});
			}
		};
		new Timer().schedule(task, effectTime);
	}
}
