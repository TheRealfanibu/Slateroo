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

public class SnakeManager {
	private List<Snake> snakes;
	
	private ItemManager itemManager;
	
	private boolean running = true;
	
	public SnakeManager(KeyboardSteering keyb, ItemManager itemManager) {
		this.itemManager = itemManager;
		
		Item.setSnakeManager(this);
		ItemSpawner.setSnakeManager(this);
		
		initSnakes(keyb);
	}
	
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
	
	public void checkItemIntersections() {
		for(Snake snake : snakes) {
			for(Item item : itemManager.getItems()) {
				if(!snake.isCollided())
					item.checkSnakeIntersection(snake);
			}
		}
	}
	
	public void takeActionForEachSnake() {
		snakes.forEach(snake -> snake.takeAction());	
	}
	
	public void removeObsoleteSnakes() {
		snakes.removeIf(snake -> !snake.isVisible());
		if(snakes.isEmpty())
			running = false;
	}
	
	public void render(Graphics g) {
		snakes.forEach(snake -> snake.render(g));
	}
	
	private void initSnakes(KeyboardSteering keyb) {
		snakes = new CopyOnWriteArrayList<>();
		
		for(int i = 0; i < 4; i++) {
			snakes.add(new Snake(keyb));
		}
	}

	public List<Snake> getSnakes() {
		return snakes;
	}
	
	public List<Snake> getSnakesExceptThisSnake(Snake snake) {
		return snakes.stream().filter(snk -> snk != snake).collect(Collectors.toList());
	}

	public boolean isGameRunning() {
		return running;
	}
}
