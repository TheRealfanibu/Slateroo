package items;

import items.manage.ItemEffectMode;
import items.superClasses.MultiplePlayerEffectItem;
import logic.Snake;

public class HeadToTailItem extends MultiplePlayerEffectItem{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 10;

	public HeadToTailItem(int x, int y, ItemEffectMode mode) {
		super(x, y, mode, "swap");
	}

	@Override
	protected void effect(Snake snake) {
		snake.swapHeadToTail();
	}
	
}
