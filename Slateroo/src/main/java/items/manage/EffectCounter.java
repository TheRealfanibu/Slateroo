package items.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.Snake;
import logic.SnakeManager;

public class EffectCounter {
	private Map<Snake, Integer> snakeEffectCount = new HashMap<>();
	
	private Map<SnakeManager, Integer> effectCount = new HashMap<>(); // if the effect Count is not associated with a snake

	private static List<EffectCounter> allCounters = new ArrayList<>();

	public EffectCounter() {
		allCounters.add(this);
	}
	
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

	public void removeSnakeEntry(Snake key) {
		snakeEffectCount.remove(key);
	}

	public void removeSnakeManagerEntry(SnakeManager key) {
		effectCount.remove(key);
	}

	public static void removeSnakeCounter(Snake snake) {
		allCounters.forEach(counter -> counter.removeSnakeEntry(snake));
	}

	public static void removeSnakeManagerCounter(SnakeManager manager) {
		allCounters.forEach(counter -> counter.removeSnakeManagerEntry(manager));
	}
}
