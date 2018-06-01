package items;

import items.manage.ItemEffectMode;
import items.markerInterfaces.IStackableEffectItem;
import items.superClasses.MultiplePlayerTimeEffectItem;
import logic.Snake;

public class SlowItem extends MultiplePlayerTimeEffectItem implements IStackableEffectItem{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 15;
	
	private static final double EFFECT_TIME = 5;
	private static final double SLOW_FACTOR = 0.5;
	
	public SlowItem(int x, int y, ItemEffectMode mode) {
		super(x, y, mode, "ie", EFFECT_TIME);
	}

	@Override
	protected void resetEffect(Snake snake) {
		snake.changeSpeed(1 / SLOW_FACTOR);
	}
	@Override
	protected void effect(Snake snake) {
		snake.changeSpeed(SLOW_FACTOR);
	}
}
