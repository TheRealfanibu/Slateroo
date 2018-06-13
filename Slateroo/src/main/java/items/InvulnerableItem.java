package items;

import items.manage.EffectCounter;
import items.manage.ItemEffectMode;
import items.superClasses.MultiplePlayerTimeEffectItem;
import logic.Snake;
import logic.SnakeManager;

public class InvulnerableItem extends MultiplePlayerTimeEffectItem {
	public static final float INVULNERABLE_VISIBILITY = 0.35f;
	
	public static final double AVG_SPAWN_TIME = 45;
	public static final double MAX_SPAWN_DIFF = 15;
	
	private static final double EFFECT_TIME = 5;
	
	private static EffectCounter effectCounter = new EffectCounter();

	public InvulnerableItem(int x, int y, ItemEffectMode mode, SnakeManager snakeManager) {
		super(x, y, mode, snakeManager, "stern", EFFECT_TIME);
	}

	@Override
	protected void effect(Snake snake) {
		snake.setInvulnerable(true);
		effectCounter.increment(snake);
	}
	
	@Override
	protected void resetEffect(Snake snake) {
		if(effectCounter.decrement(snake))
			snake.setInvulnerable(false);
	}
	
}
