package logic;

import ai.Environment;
import gui.Frame;
import io.Direction;
import io.Steering;
import items.*;
import items.manage.EffectCounter;
import items.manage.TimeEffect;
import items.superClasses.Item;
import utilities.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * This class represents a snake consisting of multiple {@link SnakeTile}s
 * @author Jonas
 *
 */

public class Snake {
	/**
	 * The initial amount of {@code SnakeTile} objects every snakes has at the beginning of the game
	 */
	private static final int INIT_FOLLOWING_TILES = 20;
	/**
	 * The gap between {@code SnakeTile} objects when moving
	 */
	private static final int TILE_SPACING = Frame.toScaledPixelSize(25);
	/**
	 * The brightness of the last {@link SnakeTile} in {@code tiles}
	 */
	private static final double MAX_BRIGHTNESS_DROP = 0.7;
	/**
	 * The time the snake fades out after dieing in seconds
	 */
	private static final float FADE_TIME = 1;
	/**
	 * The step amount of which the snake becomes less visible every frame when it has collided
	 */
	private static final float D_FADE = (1 / FADE_TIME) / Environment.FPS;
	/**
	 * every {@code STARVATION_PERIOD} seconds one {@link SnakeTile} gets removed -> starvation
	 */
	private static final double STARVATION_PERIOD = 2.5;
	/**
	 * The amount of how many {@link Snake} objects have been instanciated
	 */
	private static final double COLLIDE_REWARD = -1;

	private static final double SURVIVE_REWARD = 0.001;

	/**
	 * Reference to the {@link Arena} instance
	 */
	private Arena arena;
	/**
	 * Indicates whether this snake is steered by an AI
	 */
	private final boolean steeredByAI;
	/**
	 * The List of {@link SnakeTile} this snakes consists of
	 */
	private List<SnakeTile> tiles;
	/**
	 * Reference to the first object in {@code tiles} which is the head of the snake
	 */
	private HeadTile head;
	/**
	 * The general color of this snake
	 */
	private Color color;
	/**
	 * To prevent ConcurrentModificationExceptions when accessing {@code tiles}. This approach is
	 * faster than a CopyOnWriteArrayList because there may be many objects in {@code tiles}
	 */
	private ReadWriteLock tilesLock; 
	/**
	 * The time effects which this snake is affected by at the moment.
	 * This has to be stored for AI Input.
	 */
	private List<TimeEffect> timeEffects;
	/**
	 * The most recent time point on which a tile was removed by starvation, measured in milliseconds since 1970.
	 * This has to be stored for AI Input
	 */
	private long lastStarvationTimePoint;
	/**
	 * Stores the visibility of this snake.
	 * "1" indicating fully visible and "0" indicating invisible.
	 * This variable is needed when the snake dies and fades out.
	 */
	private float visibility = 1;

	private int position;
	/**
	 * Indicates whether this snake is currently affected by an {@link ReverseSteeringItem}
	 */
	private boolean reverseSteering = false;
	/**
	 * Indicates whether this snake is currently affected by an {@link InvulnerableItem}
	 */
	private boolean invulnerable = false;
	/**
	 * Indicates whether this snake has collided and is going to die and fade out
	 */
	private boolean collided = false;
	/**
	 * Indicates whether this snake is visible in the game, meaning its visibility is not "0".
	 */
	private boolean visible = true;
	
	private double reward;
	
	/**
	 * Creates a snake instance
	 * @param steer The {@link Steering} instance this snake is going to be steered by
	 */
	public Snake(boolean steeredByAI, Arena arena, int position) {
		this.steeredByAI = steeredByAI;
		this.arena = arena;
		this.position = position;
		this.reward = SURVIVE_REWARD;

		tilesLock = new ReentrantReadWriteLock(true);
		if(steeredByAI)
			timeEffects = new ArrayList<>();
		
		chooseColor();
		initTiles();
		//initStarvation();

	}
	
	/**
	 * Executes the starvation process which removes a {@link SnakeTile} from this snake every {@code STARVATION_PERIOD} seconds
	 */
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
	
	/** 
	 * @return A value near "0" if the time until the next removal of a SnakeTile from this snake caused by starvation
	 * is near {@code STARVATION_PERIOD} or returns a value near "1" if the time is near "0"
	 */
	public double calcScaledTimeToNextStarvation() {
		if(collided || invulnerable)
			return 0;
		long collapsedTime = System.currentTimeMillis() - lastStarvationTimePoint;
		return collapsedTime / (STARVATION_PERIOD * 1000d);
	}
	
	/**
	 * Decides the action depending on the state of this snake.
	 * If this snake is not collided then it should move.
	 * If this snake is collided but not faded out then it should fade out.
	 */
	public void takeAction(Direction moveDir) {
		if(!collided)
			move(moveDir);
		else if(visible)
			fadeOut();
	}
	
	/**
	 * Initiates the movement for every {@link SnakeTile} of this snake.
	 */
	private void move(Direction moveDir) {
		head.move(reverseSteering ? moveDir.reverse() : moveDir);
		
		SnakeTile tileNow, tileBefore = head;
		
		tilesLock.readLock().lock();
		for(int i = 1; i < tiles.size(); i++) {
			tileNow = tiles.get(i);
			calcTileMovement(tileNow, tileBefore);
			tileBefore = tileNow;
		}
		tilesLock.readLock().unlock();
	}
	
	public void checkBorderCollision() {
		if(!invulnerable && !arena.isInTeleportMode() && arena.isSnakeCollidingWithBorder(head))
			collide();
	}
	
	/**
	 * Calculates the movement of a SnakeTile depending on its SnakeTile ahead in the snake
	 * @param tileNow The SnakeTile to move
	 * @param tileAhead The SnakeTile ahead
	 */
	private void calcTileMovement(SnakeTile tileNow, SnakeTile tileAhead) {
		double dx = tileAhead.getTeleportX() - tileNow.getTeleportX();
		double dy = tileAhead.getTeleportY() - tileNow.getTeleportY();
		
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
	
	/**
	 * Calculates the move angle of this SnakeTile.
	 * The Angle starts with 0 degrees at the top and the angle increases up to 360 degrees clockwise
	 * @param slope The direction of the movement, represented as a slope line
	 * @param dx To Indicate whether the SnakeTile is moving left or right on this slope line
	 * @return The move angle of this SnakeTile in degrees
	 */
	private double calcTileMoveAngle(double slope, double dx) {
		double moveAngle = Math.toDegrees(Math.atan(-1 / slope)); // reverse value of slope = dx / dy and negative because + 180 Grad
		if(moveAngle > 0 && dx < 0 || moveAngle < 0 && dx > 0)
			moveAngle += 180;
		return moveAngle;
	}
	
	/**
	 * A step of the fade out when the snake has collided
	 */
	private void fadeOut() {
		visibility -= D_FADE;
		if(visibility <= 0)
			visible = false;
	}
	
	/**
	 * Adds the specified amount of {@link SnakeTile} to the tail of the snake.
	 * @param amount The specified amount
	 */
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
	
	/**
	 * Removes the specified amount of {@link SnakeTile} from the tail of the snake.
	 * @param amount The specified amount
	 */
	public void removeTilesFromTail(int amount) {
		tilesLock.writeLock().lock();
		for(int i = 0; i < amount; i++) {
			tiles.remove(tiles.size() - 1);
		}
		tilesLock.writeLock().unlock();
	}
	
	/**
	 * Renders the whole snake to the frame
	 * @param g the Graphics object to draw with
	 */
	public void render(Graphics g) {
		if(!visible)
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		if(visibility != 1) {
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, visibility);
			g2.setComposite(alpha);
		}
		tilesLock.readLock().lock();
		for(int i = tiles.size() - 1; i >= 0; i--) { // start with the last tile, to draw the head over every other tile -> nice 3D Effect
			tiles.get(i).render(g2);
		}
		tilesLock.readLock().unlock();
		
		if(visibility != 1)
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
	}
	
	/**
	 * Allocates a different brightness color to the tiles of this snake depending on the amount of tiles this snake has and
	 * the {@code MAX_BRIGHTNESS_DROP}
	 */
	
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
	
	/**
	 * 
	 * @param brightFactor The Brightness this color should have
	 * @return A Color object with the specified brightness
	 */
	private Color calcColor(double brightFactor) {
		return new Color((int) (color.getRed() * brightFactor),
				(int) (color.getGreen() * brightFactor),
				(int) (color.getBlue() * brightFactor));
	}
	
	/**
	 * Initializes the SnakeTiles of this snake and calculating its position
	 */
	private void initTiles() {
		tiles = new ArrayList<>();
		
		final int gapToBorderX = 30, gapToBorderY = 200; 
		boolean onTheRight = position < 2; // location: 0 = upper right; 1 = lower right; 2 = upper left; 3 = lower left
		boolean onTheTop = position % 2 == 0;
		
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
	
	/**
	 * Reverses the order of {@code tiles}. The head becomes the tail and the tails becomes the head.
	 * This method is executed when collecting a {@link HeadToTailItem}.
	 */
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
	
	/**
	 * Reverses the {@code reverseSteering} flag.
	 * This method is executed when collecting a {@link ReverseSteeringItem}
	 */
	public void reverseSteering() {
		reverseSteering = !reverseSteering;
	}
	
	/**
	 * Handles the {@code invulnerale} flag and the visibility of the snake.
	 * This method is executed when collecting a {@link InvulnerableItem}
	 * @param invul
	 */
	public void setInvulnerable(boolean invul) {
		invulnerable = invul;
		if(invul)
			visibility = InvulnerableItem.INVULNERABLE_VISIBILITY;
		else
			visibility = 1;
	}
	
	/**
	 * Updates {@code lastStarvationTimePoint} with the current time
	 */
	private void setStarvationTimePoint() {
		lastStarvationTimePoint = System.currentTimeMillis();
	}
	/**
	 * Assigns the specified color to the snake.
	 */
	private void chooseColor() {
		Color[] colors = {new Color(0,255,127), new Color(250,128,114), new Color(176,224,230), new Color(240,230,140)};
		color = colors[position];
	}
	/**
	 * Changes the movement speed of this snake.
	 * This method is executed when collecting a {@link SpeedItem} or a {@link SlowItem} or if the effect is reseted
	 * @param factor The factor by which the speed of the snake gets multiplied
	 */
	public void changeVelocity(double velocityAmount) {
		head.changeVelocity(velocityAmount);
	}
	/**
	 * Changes the speed of rotation of this snake.
	 * This method is executed when collecting a {@link SlowSteerItem} or if the effect is reseted
	 * @param angleAmount The factor by which the speed of the snake gets multiplied
	 */
	public void changeSteerAngle(double angleAmount) {
		head.changeSteerAngle(angleAmount);
	}
	/**
	 * Starts or Stops the Zick Zack Movement Depending on the specified mode
	 * This method is executed when collecting a {@link ZickZackMovementItem} or if the effect is reseted
	 * @param mode Whether to start or stop the Zick Zack Movement
	 */
	public void setZickZackMode(boolean mode) {
		head.setZickZackMode(mode);
	}
	
	/**
	 * Adds a TimeEffect instance to {@code timeEffects}
	 * @param effect The TimeEffect to add
	 */
	public void addTimeEffect(TimeEffect effect) {
		if(steeredByAI)
			timeEffects.add(effect);
	}
	/**
	 * Removes a TimeEffect instance from {@code timeEffects}
	 * @param effect The TimeEffect to remove
	 */
	public void removeTimeEffect(TimeEffect effect) {
		if(steeredByAI)
			timeEffects.remove(effect);
	}
	
	/**
	 * Returns all TimeEffects in {@code timeEffects} which are from the specified {@link Item} class
	 * @param itemClass the specified Item class
	 * @return A List of the Time Effects which are from the specified Item class
	 */
	public List<TimeEffect> getItemClassTimeEffects(Class<?> itemClass) {
		return timeEffects.stream().filter(timeEffect -> timeEffect.getItemClass() == itemClass).collect(Collectors.toList());
	}
	
	public double getAndResetReward() {
		double rewardSave = this.reward;
		this.reward = SURVIVE_REWARD;
		return rewardSave;
	}
	
	public void collide() {
		this.collided = true;
		addReward(COLLIDE_REWARD);
		EffectCounter.removeSnakeCounter(this);
	}
	
	public void addReward(double reward) {
		this.reward += reward;
	}
	
	public boolean isSteeredByAI() {
		return steeredByAI;
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
	
	public ReadWriteLock getTilesLock() {
		return tilesLock;
	}
}
