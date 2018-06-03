package items;

import items.manage.ItemEffectMode;
import items.markerInterfaces.IStackableEffectItem;
import items.superClasses.MultiplePlayerTimeEffectItem;
import logic.Snake;

public class SlowSteerItem extends MultiplePlayerTimeEffectItem implements IStackableEffectItem{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 15;
	
	private static final double EFFECT_TIME = 5;
	private static final double STEER_ANGLE_CHANGE = -100;

	public SlowSteerItem(int x, int y, ItemEffectMode mode) {
		super(x, y, mode, "bananenschale", EFFECT_TIME);
	}

	@Override
	protected void effect(Snake snake) {
		snake.changeSteerAngle(STEER_ANGLE_CHANGE);
	}	
	@Override
	protected void resetEffect(Snake snake) {
		snake.changeSteerAngle(-STEER_ANGLE_CHANGE);
	}
}
