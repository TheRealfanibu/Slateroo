package items;

import items.manage.EffectCounter;
import items.manage.ItemEffectMode;
import items.superClasses.MultiplePlayerTimeEffectItem;
import logic.Snake;
import logic.SnakeManager;

public class ZickZackMovementItem extends MultiplePlayerTimeEffectItem{
	public static final int ZICK_ZACK_ANGLE_CHANGE = 90;
	
	public static final double AVG_SPAWN_TIME = 60;
	public static final double MAX_SPAWN_DIFF = 30;
	
	private static final double EFFECT_TIME = 10;
	
	private static final EffectCounter effectCounter = new EffectCounter();
	
	public ZickZackMovementItem(int x, int y, ItemEffectMode mode, SnakeManager snakeManager) {
		super(x, y, mode, snakeManager, "zickzack", EFFECT_TIME);
	}

	@Override
	protected void effect(Snake snake) {
		effectCounter.increment(snake);
		snake.setZickZackMode(true);
	}
	
	@Override
	protected void resetEffect(Snake snake) {
		if(effectCounter.decrement(snake))
			snake.setZickZackMode(false);
	}

}
