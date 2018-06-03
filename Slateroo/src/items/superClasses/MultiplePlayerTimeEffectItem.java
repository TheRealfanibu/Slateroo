package items.superClasses;

import java.util.Timer;
import java.util.TimerTask;

import items.manage.ItemEffectMode;
import items.manage.TimeEffect;
import items.markerInterfaces.ITimeEffectItem;
import logic.Snake;
/**
 * This superclass represents item which are {@link TimeEffectItem}s and also {@link MultiplePlayerEffectItem}s at the same time.
 * Due to the non-existence of multiple inheritance which would be benefical here, this class inherits from {@link MultiplePlayerEffectItem}
 * because more code can be saved.
 * @author Jonas
 *
 */
public abstract class MultiplePlayerTimeEffectItem extends MultiplePlayerEffectItem implements ITimeEffectItem{
	/**
	 * The amount of time how long an effect should be activated, measured in milliseconds.
	 */
	private final long effectTime;
	/**
	 * Creates an item object with the specified coordinates, effect mode, icon and effect time
	 * @param x The x-coordinate of the middle point
	 * @param y The y-coordinate of the middle point
	 * @param mode The effect mode of this item, see {@link MultiplePlayerEffectItem}
	 * @param iconName The name of the image file that contains the look of this item
	 * @param effectTime The time the effect of this item should be activated in seconds
	 */
	public MultiplePlayerTimeEffectItem(int x, int y, ItemEffectMode mode, String iconName, double effectTime) {
		super(x, y, mode, iconName);
		
		this.effectTime = (long) (effectTime * 1000);
	}
	/**
	 * Every subclass of this class must override this method. This method specifies the behaviour when the effect is reseted
	 * @param snake The snake which collected the item
	 */
	protected abstract void resetEffect(Snake snake);
	
	@Override
	protected void intersectionHandling(Snake snake) {
		super.intersectionHandling(snake);
		TimeEffect effect = new TimeEffect(effectTime, getClass());
		executeOnSnakeDependingOnMode(snake, snk -> snk.addTimeEffect(effect));
		TimerTask resetTask = new TimerTask() {
			public void run() {
				executeOnSnakeDependingOnMode(snake, snk ->  {
					resetEffect(snk);
					snk.removeTimeEffect(effect);
				});
			}
		};
		new Timer().schedule(resetTask, effectTime);
	}
}
