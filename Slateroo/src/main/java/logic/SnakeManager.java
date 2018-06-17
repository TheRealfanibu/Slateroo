package logic;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

import game.IntersectionUtils;
import io.Direction;
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
	private static final double KILLING_REWARD = 0.5;
	
	/**
	 * Contains every Snake visible in the game
	 */
	private List<Snake> snakes;

	/**
	 * Indicates whether the game is running
	 */
	private boolean gameRunning = true;
	
	public SnakeManager(int snakeAmount, int playerAmount, Arena arena) {
		
		initSnakes(snakeAmount, playerAmount, arena);
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
					dierSnake.collide();
					killerSnake.addReward(KILLING_REWARD);
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
	public void checkSnakeBorderCollision() {
		snakes.forEach(snake -> {
			if(!snake.isCollided())
				snake.checkBorderCollision();
		});
	}
	
	/**
	 * Calls the {@code takeAction()} method for every {@link Snake}
	 */
	public void takeActionForEachSnake(Direction[] snakeActions) {
		for(int i = 0; i < snakes.size(); i++) {
			snakes.get(i).takeAction(snakeActions[i]);
		}
	}
	
	/**
	 * Removes snakes from {@code snakes} which have collided and aren't visible anymore.
	 *
	 */
	public void removeObsoleteSnakes() {
		snakes.removeIf(snake -> !snake.isVisible());
		if(snakes.isEmpty())
			gameRunning = false;
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
	private void initSnakes(int snakeAmount, int playerAmount, Arena arena) {
		snakes = new CopyOnWriteArrayList<>();
		
		for(int i = 0; i < snakeAmount; i++) {
			if(i < playerAmount)
				snakes.add(new Snake(false, arena, i));
			else
				snakes.add(new Snake(true, arena, i));
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
	
	public int getSnakeAmount() {
		return snakes.size();
	}
	
	public List<Snake> getSnakes() {
		return snakes;
	}

	public boolean isEverySnakeCollided() {
		return !gameRunning || (snakes.size() == 1 && snakes.get(0).isCollided());
	}

	public boolean isGameRunning() {
		return gameRunning;
	}
}
