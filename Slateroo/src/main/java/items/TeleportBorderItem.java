package items;

import items.manage.EffectCounter;
import items.superClasses.TimeEffectItem;
import logic.Arena;
import logic.Snake;
import logic.SnakeManager;

public class TeleportBorderItem extends TimeEffectItem{
	public static final double AVG_SPAWN_TIME = 10;
	public static final double MAX_SPAWN_DIFF = 5;
	
	private static final double EFFECT_TIME = 15;
	
	private static Arena arena;
	
	private static EffectCounter effectCounter = new EffectCounter();

	public TeleportBorderItem(int x, int y, SnakeManager snakeManager) {
		super(x, y, snakeManager, "teleport", EFFECT_TIME);
	}

	@Override
	protected void effect(Snake snake) {
		arena.setTeleportMode(true);
		effectCounter.increment(snakeManager);
	}

	@Override
	protected void resetEffect(Snake snake) {
		if(effectCounter.decrement(snakeManager))
			arena.setTeleportMode(false);
	}
	
	public static void setArena(Arena arena) {
		TeleportBorderItem.arena = arena;
	}

}