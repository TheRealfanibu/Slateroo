package items.manage;

import static items.superClasses.Item.LENGTH;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import game.IntersectionUtils;
import items.superClasses.Item;
import logic.ObjectSequence;
import logic.Snake;
import logic.SnakeManager;

public class ItemManager {
	private static final int MAX_ITEM_AMOUNT = 10;
	
	private SnakeManager snakeManager;

	
	private List<Item> items;
	
	public ItemManager(SnakeManager snakeManager) {
		this.snakeManager = snakeManager;
		items = new CopyOnWriteArrayList<>(); // rendering happens on different thread -> removing items could lead to ConcurrentModificationException
											  // thats why we are using a thread-safe list
	}
	
	private void createItemSpawners() {
		for(Class<?> itemClass : ObjectSequence.getItemClasses()) // create Item Spawners
			new ItemSpawner(this, snakeManager, itemClass);
	}
	
	/**
	 * Checks for every snake whether the snake is intersecting the item and therefore collecting it
	 */
	public void checkSnakeItemIntersections() {
		for(Snake snake : snakeManager.getSnakes()) {
			for(Item item : items) {
				if(!snake.isCollided())
					item.checkSnakeIntersection(snake);
			}
		}
	}
	
	public boolean isItemIntersectingExistingItem(Point coords) {	
		for(Item item : items) {
			if(IntersectionUtils.isIntersectingRectangle(coords.getX(), coords.getY(), item.getX(), item.getY(), LENGTH, LENGTH, LENGTH, LENGTH))
				return true;
		}
		return false;
	}
	
	public void addItem(Item item) {
		if(items.size() < MAX_ITEM_AMOUNT)
			items.add(item);
	}
	
	public void render(Graphics g) {
		items.forEach(item -> item.render(g));
	}
	
	public void removeActivatedItems() {
		items.removeIf(item -> item.isEffectActivated());
	}
	
	public List<Item> getItems() {
		return items;
	}
}
