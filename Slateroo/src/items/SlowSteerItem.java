package items;

import items.manage.ItemEffectMode;
import items.markerInterfaces.IStackableEffectItem;
import items.superClasses.MultiplePlayerTimeEffectItem;
import logic.Snake;

public class SlowSteerItem extends MultiplePlayerTimeEffectItem implements IStackableEffectItem{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 15;
	
	private static final double EFFECT_TIME = 5;
	private static final double STEER_ANGLE_SLOW_FACTOR = 0.6;

	public SlowSteerItem(int x, int y, ItemEffectMode mode) {
		super(x, y, mode, "bananenschale", EFFECT_TIME);
	}

	@Override
	protected void resetEffect(Snake snake) {
		snake.changeSteerAngle(1 / STEER_ANGLE_SLOW_FACTOR);
	}

	@Override
	protected void effect(Snake snake) {
		snake.changeSteerAngle(STEER_ANGLE_SLOW_FACTOR);
	}	

}
