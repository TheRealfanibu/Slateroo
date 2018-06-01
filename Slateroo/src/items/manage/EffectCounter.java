package items.manage;

import java.util.HashMap;
import java.util.Map;

import logic.Snake;

public class EffectCounter {
	private Map<Snake, Integer> snakeEffectCount = new HashMap<>();
	
	private int effectCount = 0; // if the effect Count is not associated with a snake
	
	public void increment() {
		effectCount++;
	}
	
	public boolean decrement() {
		effectCount--;
		return effectCount == 0;
	}
	
	public void increment(Snake snake) {
		snakeEffectCount.merge(snake, 1, Integer::sum);
	}

	public boolean decrement(Snake snake) {
		int newCount = snakeEffectCount.computeIfPresent(snake, (snk, val) -> val - 1);
		return newCount == 0;
	}
}
