package logic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import gui.Frame;
import io.Direction;
import io.Steering;
import items.InvulnerableItem;
import items.manage.TimeEffect;
import utilities.Utils;

public class Snake {
	private static final int INIT_FOLLOWING_TILES = 20; // the tiles following the head
	private static final int TILE_SPACING = Frame.toScaledPixelSize(25); // amount of the gap between tiles in pixels
	
	private static final double MAX_BRIGHTNESS_DROP = 0.7; // brightness of the last snake tile
	
	private static final float FADE_TIME = 1; // Time the snake fades out after dieing in seconds
	private static final float D_FADE = (1 / FADE_TIME) / Main.FPS;
	
	private static final double STARVATION_PERIOD = 2.5; // every STARVATION_PERIOD seconds one tiles gets removed -> starvation
	
	private static int instances = 0;
	
	private static Arena arena;
	
	private Steering steerManager;
	private final boolean steeredByAI;
	
	private List<SnakeTile> tiles;
	private HeadTile head; // reference to the first object in "tiles" -> head of the snake
	
	private Color color;
	
	private ReadWriteLock tilesLock;
	
	private List<TimeEffect> timeEffects;
	private long lastStarvationTimePoint;
	
	private float visibility = 1;
	
	private boolean reverseSteering = false;
	private boolean invulnerable = false;
	
	private boolean collided = false;
	private boolean visible = true;
	
	public Snake(Steering steer) {
		instances++;
			
		this.steerManager = steer;
		
		tilesLock = new ReentrantReadWriteLock(true);
		steeredByAI = true;//steer.isAISteering();
		if(steeredByAI)
			timeEffects = new ArrayList<>();
		
		chooseColor();
		initTiles();
		initStarvation();
	}
	
	public void addTimeEffect(TimeEffect effect) {
		if(steeredByAI)
			timeEffects.add(effect);
	}
	
	public void removeTimeEffect(TimeEffect effect) {
		if(steeredByAI)
			timeEffects.remove(effect);
	}
	
	public List<TimeEffect> getItemClassTimeEffects(Class<?> itemClass) {
		return timeEffects.stream().filter(timeEffect -> timeEffect.getItemClass() == itemClass).collect(Collectors.toList());
	}
	
	private void initStarvation() {
		Timer timer = new Timer();
		TimerTask starvationTask = new TimerTask() {
			public void run() {
				setStarvationTimePoint();
				if(!invulnerable) {
					if(tiles.size() > 1 && !collided) {
						removeTilesFromTail(1);
						updateTileColors();
					}
					else
						collided = true;
				}
			}
		};
		setStarvationTimePoint();
		long periodInMillis = (long) (STARVATION_PERIOD * 1000);
		timer.scheduleAtFixedRate(starvationTask, periodInMillis, periodInMillis);
	}
	
	public double calcScaledTimeToNextStarvation() {
		if(collided || invulnerable)
			return 0;
		long collapsedTime = System.currentTimeMillis() - lastStarvationTimePoint;
		return collapsedTime / (STARVATION_PERIOD * 1000d);
	}
	
	public void takeAction() {
		if(!collided)
			move();
		else if(visible)
			fadeOut();
	}
	
	private void move() {
		Direction moveDir = steerManager.getMoveDirection();
		head.move(reverseSteering ? moveDir.reverse() : moveDir);
		
		SnakeTile tileNow, tileBefore = head;
		
		tilesLock.readLock().lock();
		for(int i = 1; i < tiles.size(); i++) {
			tileNow = tiles.get(i);
			calcTileMovement(tileNow, tileBefore);
			tileBefore = tileNow;
		}
		tilesLock.readLock().unlock();
		
		if(!invulnerable && !arena.isInTeleportMode() && arena.isSnakeColliding(head))
			collided = true;
			
	}
	
	private void calcTileMovement(SnakeTile tileNow, SnakeTile tileBefore) {
		double dx = tileBefore.getTeleportX() - tileNow.getTeleportX();
		double dy = tileBefore.getTeleportY() - tileNow.getTeleportY();
		
		double step_dx, step_dy;
		if(dx != 0) {
			double distance = Math.sqrt(dx * dx + dy * dy);
			double slope = dy / dx;
			tileNow.setMoveAngle(calcTileMoveAngle(slope, dx));
			
			double step = head.getDVelocity() * distance / TILE_SPACING;
			step_dx = Math.cos(Math.atan(slope)) * step;
			step_dx = dx > 0 ? step_dx : - step_dx;
			step_dy = slope * step_dx;
		}
		else {
			step_dx = 0;
			step_dy = head.getDVelocity() * dy / TILE_SPACING;
			if(step_dy != 0)
				tileNow.setMoveAngle(step_dy < 0 ? 0 : 180);
		}
		
		tileNow.move(step_dx, step_dy);
	}
	
	private double calcTileMoveAngle(double slope, double dx) {
		double moveAngle = Math.toDegrees(Math.atan(-1 / slope)); // reverse value = dx / dy and negative because + 180 Grad
		if(moveAngle > 0 && dx < 0 || moveAngle < 0 && dx > 0)
			moveAngle += 180;
		return moveAngle;
	}
	
	private void fadeOut() {
		visibility -= D_FADE;
		if(visibility <= 0)
			visible = false;
	}
	
	public void addTilesToTail(int amount) {
		tilesLock.readLock().lock();
		SnakeTile lastTile = tiles.get(tiles.size() - 1);
		tilesLock.readLock().unlock();
		
		double lastTileX = lastTile.getTeleportX();
		double lastTileY = lastTile.getTeleportY();
		
		tilesLock.writeLock().lock();
		for(int i = 0; i < amount; i++) {
			tiles.add(new SnakeTile(lastTileX, lastTileY));
		}
		tilesLock.writeLock().unlock();
		updateTileColors();
	}
	
	public void removeTilesFromTail(int amount) {
		tilesLock.writeLock().lock();
		for(int i = 0; i < amount; i++) {
			tiles.remove(tiles.size() - 1);
		}
		tilesLock.writeLock().unlock();
	}
	
	public void render(Graphics g) {
		if(!visible)
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		if(visibility != 1) {
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, visibility);
			g2.setComposite(alpha);
		}
		tilesLock.readLock().lock();
		for(int i = tiles.size() - 1; i >= 0; i--) {
			tiles.get(i).render(g2);
		}
		tilesLock.readLock().unlock();
		
		if(visibility != 1)
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
	}
	
	private void updateTileColors() {
		double stepBrightDown = (1 - MAX_BRIGHTNESS_DROP) / tiles.size();
		double brightFactor = 1;
		Color tileColor;
		
		tilesLock.readLock().lock();
		for(SnakeTile tile : tiles) {
			tileColor = calcColor(brightFactor);
			tile.setColor(tileColor);
			brightFactor -= stepBrightDown;
		}
		tilesLock.readLock().unlock();
	}
	
	private Color calcColor(double brightFactor) {
		return new Color((int) (color.getRed() * brightFactor),
				(int) (color.getGreen() * brightFactor),
				(int) (color.getBlue() * brightFactor));
	}
	
	private void initTiles() {
		tiles = new ArrayList<>();
		
		final int gapToBorderX = 30, gapToBorderY = 200; 
		boolean onTheRight = instances < 3; // location: 1 = upper right 2 = lower right 3 = upper left 4 = lower left
		boolean onTheTop = instances % 2 == 1;
		
		int headX, headY;
		if(onTheRight)
			headX = Arena.WIDTH - gapToBorderX - INIT_FOLLOWING_TILES * TILE_SPACING - SnakeTile.RADIUS;
		else
			headX = gapToBorderX + INIT_FOLLOWING_TILES * TILE_SPACING + SnakeTile.RADIUS;
			
		if(onTheTop)
			headY = gapToBorderY + SnakeTile.RADIUS;
		else
			headY = Arena.HEIGHT - gapToBorderY - SnakeTile.RADIUS;
			
		
		head = new HeadTile(headX, headY, onTheRight);
		tiles.add(head);
		
		int xAdd = onTheRight ? TILE_SPACING : - TILE_SPACING;
		for(int i = 0; i < INIT_FOLLOWING_TILES; i++) {
			headX += xAdd;
			tiles.add(new SnakeTile(headX, headY));
		}
		updateTileColors();
	}
	
	public void swapHeadToTail() {
		tilesLock.readLock().lock();
		List<SnakeTile> tilesBackup = Collections.unmodifiableList(Utils.deepCopy(tiles));
		int tileAmount = tiles.size();
		tilesLock.readLock().unlock();
		
		tilesLock.writeLock().lock();
		for(int i = 0; i < tileAmount; i++) {
			SnakeTile tile = tiles.get(i);
			SnakeTile destTile = tilesBackup.get(tileAmount - 1 - i);
			
			tile.setTeleportX(destTile.getTeleportX());
			tile.setTeleportY(destTile.getTeleportY());
			
			double newMoveAngle = destTile.getMoveAngle() + 180; // reverse direction
			tile.setMoveAngle(newMoveAngle);
			if(tile instanceof HeadTile)
				((HeadTile) tile).setCircleAngle(newMoveAngle - 90);
		}
		tilesLock.writeLock().unlock();
	}
	
	public void reverseSteering() {
		reverseSteering = reverseSteering ? false : true;
	}
	
	public void setInvulnerable(boolean invul) {
		invulnerable = invul;
		if(invul)
			visibility = InvulnerableItem.INVULNERABLE_VISIBILITY;
		else
			visibility = 1;
	}
	
	private void setStarvationTimePoint() {
		lastStarvationTimePoint = System.currentTimeMillis();
	}
	
	private void chooseColor() {
		Color[] colors = {new Color(0,255,127), new Color(250,128,114), new Color(176,224,230), new Color(240,230,140)};
		color = colors[instances - 1];
	}
	
	public void changeSpeed(double factor) {
		head.changeSpeed(factor);
	}
	
	public void changeSteerAngle(double factor) {
		head.changeSteerAngle(factor);
	}
	
	public void setZickZackMode(boolean mode) {
		head.setZickZackMode(mode);
	}
	
	public HeadTile getHead() {
		return head;
	}
	
	public List<SnakeTile> getTiles() {
		return tiles;
	}
	
	public float getVisibility() {
		return visibility;
	}

	public boolean isCollided() {
		return collided;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isInvulnerable() {
		return invulnerable;
	}
	
	public void setCollided(boolean collided) {
		this.collided = collided;
	}
	
	public ReadWriteLock getTilesLock() {
		return tilesLock;
	}
	
	public static void setArena(Arena arena) {
		Snake.arena = arena;
	}
}
