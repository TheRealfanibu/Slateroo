package items;

import items.manage.ItemEffectMode;
import items.superClasses.MultiplePlayerEffectItem;
import logic.Snake;

public class ReverseSteeringItem extends MultiplePlayerEffectItem{
	public static final int AVG_SPAWN_TIME = 40;
	public static final int MAX_SPAWN_DIFF = 20;
	
	//private static final int EFFECT_TIME = 10;
	
	public ReverseSteeringItem(int x, int y, ItemEffectMode mode) {
		super(x, y, mode, "reverse");
	}
	

	@Override
	protected void effect(Snake snake) {
		snake.reverseSteering();
	}

}
