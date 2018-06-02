package items;

import items.manage.ItemEffectMode;
import items.markerInterfaces.IStackableEffectItem;
import items.superClasses.MultiplePlayerTimeEffectItem;
import logic.Snake;

public class SpeedItem extends MultiplePlayerTimeEffectItem implements IStackableEffectItem{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 15;
	
	private static final double EFFECT_TIME = 5;
	private static final double SPEED_CHANGE = 150;
	
	public SpeedItem(int x, int y, ItemEffectMode mode) {
		super(x, y, mode, "nitro", EFFECT_TIME);
	}

	@Override
	protected void effect(Snake snake) {
		snake.changeSpeed(SPEED_CHANGE);
	}
	
	@Override
	protected void resetEffect(Snake snake) {
		snake.changeSpeed(-SPEED_CHANGE);
	}
}
