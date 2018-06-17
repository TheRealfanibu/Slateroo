package items.manage;

import java.util.HashMap;
import java.util.Map;

import logic.Snake;
import logic.SnakeManager;

public class EffectCounter {
	private Map<Snake, Integer> snakeEffectCount = new HashMap<>();
	
	private Map<SnakeManager, Integer> effectCount = new HashMap<>(); // if the effect Count is not associated with a snake
	
	
	public void increment(SnakeManager manager) {
		effectCount.merge(manager, 1, Integer::sum);
	}
	
	public boolean decrement(SnakeManager manager) {
		int newCount = effectCount.computeIfPresent(manager, (SnakeManager mng, Integer count) -> count - 1);
		return newCount == 0;
	}
	
	public void increment(Snake snake) {
		snakeEffectCount.merge(snake, 1, Integer::sum);
	}

	public boolean decrement(Snake snake) {
		int newCount = snakeEffectCount.computeIfPresent(snake, (Snake snk, Integer val) -> val - 1);
		return newCount == 0;
	}
}
