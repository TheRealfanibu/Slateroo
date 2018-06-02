package logic;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

import game.IntersectionUtils;
import io.KeyboardSteering;
import items.manage.ItemManager;
import items.manage.ItemSpawner;
import items.superClasses.Item;

/**
 * The purpose of this class is to manage the multiple {@link Snake} instances in the game.
 * There will only be one instance of this class
 * @author Jonas
 *
 */
public class SnakeManager {
	/**
	 * Contains every Snake visible in the game
	 */
	private List<Snake> snakes;
	/**
	 * Reference to the Item Manager
	 */
	private ItemManager itemManager;
	/**
	 * Indicates whether the game is running
	 */
	private boolean running = true;
	
	public SnakeManager(KeyboardSteering keyb, ItemManager itemManager) {
		this.itemManager = itemManager;
		
		Item.setSnakeManager(this);
		ItemSpawner.setSnakeManager(this);
		
		initSnakes(keyb);
	}
	/**
	 * Checks for every {@link Snake} in the game if it collides with another Snake. If so, then
	 * the {@code collided} flag of the snake will be set to {@code true}.
	 * If two snakes collide with their head against each other, then both snakes will die.
	 */
	public void checkSnakeCrashes() {
		for(int i = 0; i < snakes.size(); i++) {
			Snake dierSnake = snakes.get(i);
			if(dierSnake.isCollided() || dierSnake.isInvulnerable())
				continue;
			for(int j = 0; j < snakes.size(); j++) {
				if(j == i)
					continue;
				Snake killerSnake = snakes.get(j);
				if(killerSnake.isInvulnerable() || (killerSnake.isCollided() && killerSnake.getVisibility() != 1))
					continue;
				
				if(isSnakeCrashingIntoOtherSnake(dierSnake, killerSnake)) {
					dierSnake.setCollided(true);
					break;
				}
			}
		}
	}
	/**
	 * Checks whether a snake is colliding with another snake
	 * @param dierSnake The snake which may collide
	 * @param killerSnake The snake which {@code dierSnake} may collide with
	 * @return true if the {@code dierSnake} collides with {@code killerSnake}, else false
	 */
	private boolean isSnakeCrashingIntoOtherSnake(Snake dierSnake, Snake killerSnake) {
		double headX = dierSnake.getHead().getX();
		double headY = dierSnake.getHead().getY();
		
		ReadWriteLock tilesLock = killerSnake.getTilesLock();
		tilesLock.readLock().lock();
		for(SnakeTile tile : killerSnake.getTiles()) {
			if(IntersectionUtils.isIntersectingCircle(headX, headY, tile.getX(), tile.getY(), SnakeTile.RADIUS, SnakeTile.RADIUS)) {
				tilesLock.readLock().unlock();
				return true;
			}
		}
		tilesLock.readLock().unlock();
		return false;
	}
	/**
	 * Checks whether specified coordinates for an item are colliding with any {@code SnakeTile} from any {@code Snake}.
	 * This method is used when spawning a new item. The new item is not allowed to intersect any {@code SnakeTile}
	 * when spawning
	 * @param itemCoords
	 * @return
	 */
	public boolean isItemIntersectingSnakeTile(Point itemCoords) {
		for(Snake snake : snakes) {
			ReadWriteLock tilesLock = snake.getTilesLock();
			tilesLock.readLock().lock();
			for(SnakeTile tile : snake.getTiles()) {
				if(IntersectionUtils.isIntersectingRectangleCircle(itemCoords.getX(), itemCoords.getY(), tile.getX(), tile.getY(),
						Item.LENGTH, Item.LENGTH, SnakeTile.RADIUS)) {
					tilesLock.readLock().unlock();
					return true;
				}
			}
			tilesLock.readLock().unlock();
		}
		return false;
	}
	/**
	 * Checks for every snake whether the snake is intersecting the item and therefore collecting it
	 */
	public void checkSnakeItemIntersections() {
		for(Snake snake : snakes) {
			for(Item item : itemManager.getItems()) {
				if(!snake.isCollided())
					item.checkSnakeIntersection(snake);
			}
		}
	}
	/**
	 * Calls the {@code takeAction()} method for every {@link Snake}
	 */
	public void takeActionForEachSnake() {
		snakes.forEach(snake -> snake.takeAction());	
	}
	
	/**
	 * Removes snakes from {@code snakes} which have collided and aren't visible anymore.
	 *
	 */
	public void removeObsoleteSnakes() {
		snakes.removeIf(snake -> !snake.isVisible());
		if(snakes.isEmpty())
			running = false;
	}
	/**
	 * Renders every snake to the frame
	 * @param g The graphics object to be drawn with
	 */
	public void render(Graphics g) {
		snakes.forEach(snake -> snake.render(g));
	}
	/**
	 * Initializes the amount of snakes wanted
	 * @param keyb The user input for steering the snake with a keyboard
	 */
	private void initSnakes(KeyboardSteering keyb) {
		snakes = new CopyOnWriteArrayList<>();
		
		for(int i = 0; i < 4; i++) {
			snakes.add(new Snake(keyb));
		}
	}
	
	/**
	 * 
	 * @param snake The snake which should not be returned
	 * @return All Snakes except the specified snake
	 */
	public List<Snake> getSnakesExceptThisSnake(Snake snake) {
		return snakes.stream().filter(snk -> snk != snake).collect(Collectors.toList());
	}
	
	public List<Snake> getSnakes() {
		return snakes;
	}

	public boolean isGameRunning() {
		return running;
	}
}
