package items.superClasses;

import java.util.Timer;
import java.util.TimerTask;

import items.manage.TimeEffect;
import items.markerInterfaces.ITimeEffectItem;
import logic.Snake;

/**
 * This superclass represents items which are not {@link MultiplePlayerEffectItem} but have a time-based effect.
 * This means that the effect gets reseted after a certain amount of time.
 * @author Jonas
 *
 */
public abstract class TimeEffectItem extends Item implements ITimeEffectItem{
	/**
	 * The amount of time how long an effect should be activated, measured in milliseconds.
	 */
	private final long effectTime;
	/**
	 * Creates an item object with the specified coordinates, icon and effect time
	 * @param x The x-coordinate of the middle point
	 * @param y The y-coordinate of the middle point
	 * @param iconName The name of the image file that contains the look of this item
	 * @param effectTime The time the effect of this item should be activated in seconds
	 */
	public TimeEffectItem(int x, int y, String iconName, double effectTime) {
		super(x, y, iconName);
		
		this.effectTime = (long) (effectTime * 1000);
	}
	/**
	 * Every subclass of this class must override this method. This method specifies the behaviour when collecting the item.
	 * @param snake The snake which collected the item
	 */
	protected abstract void effect(Snake snake);
	/**
	 * Every subclass of this class must override this method. This method specifies the behaviour when the effect is reseted
	 * @param snake The snake which collected the item
	 */
	protected abstract void resetEffect(Snake snake);

	@Override
	protected void intersectionHandling(Snake snake) {
		effect(snake);
		TimeEffect effect = new TimeEffect(effectTime, getClass());
		snakeManager.getSnakes().forEach(snk -> snk.addTimeEffect(effect));
		TimerTask resetTask = new TimerTask() {
			public void run() {
				resetEffect(snake);
				snakeManager.getSnakes().forEach(snk -> snk.removeTimeEffect(effect));
			}
		};
		new Timer().schedule(resetTask, effectTime);
	}
}
