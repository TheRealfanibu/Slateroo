package items.superClasses;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import game.IntersectionUtils;
import logic.Arena;
import logic.GameObject;
import logic.HeadTile;
import logic.Snake;
import logic.SnakeManager;
import logic.SnakeTile;

/**
 * This class is the superclass of every item in the game.
 * @author Jonas
 */
public abstract class Item implements GameObject{
	/**
	 * Every items hitbox is approximated to an rectangle. This variable specifies both the width and the height of
	 * that rectangle.
	 */
	public static final int LENGTH = 50;
	/**
	 * The {@code LENGTH} attribute divided by "2"
	 */
	public static final int LENGTH_HALF = LENGTH / 2;
	/**
	 * Every item has access to the snake manager object
	 */
	protected SnakeManager snakeManager;
	/**
	 * The visual look of this item in the game		
	 */
	protected final BufferedImage icon;
	/**
	 * The x-coordinate of the middle point of this item
	 */
	private final double middleX;
	/**
	 * The y-coordinate of the middle point of this item
	 */
	private final double middleY;
	/**
	 * The draw x-coordinate. This is needed because if the middle point x-coordinate of an item is {@code LENGTH_HALF}, then it should be drawn
	 * such that it touches the left border of the arena not the left border of the frame. 
	 */
	protected final int drawX;
	/**
	 * The draw y-coordinate. This is needed because if the middle point y-coordinate of an item is {@code LENGTH_HALF}, then it should be drawn
	 * such that it touches the upper border of the arena not the upper border of the frame. 
	 */
	protected final int drawY;

	private boolean effectActivated = false;

	protected boolean disabled = false;
	
	/**
	 * Creates a new item object with the specified middle point coordinates and the specified loook
	 * @param middleX The x-coordinate of the middle point
	 * @param middleY The y-coordinate of the middle point
	 * @param iconName The name of the image file that contains the look of this item
	 */
	public Item(int middleX, int middleY,  SnakeManager snakeManager, String iconName) {
		this.middleX = middleX;
		this.middleY = middleY;
		this.snakeManager = snakeManager;
		
		drawX = middleX - LENGTH_HALF + Arena.BORDER_HITBOX;
		drawY = middleY - LENGTH_HALF + Arena.BORDER_HITBOX;
		
		icon = loadIcon(iconName);
	}
	/**
	 * This method is called when a snake collects this item. Every subclass must override this to specify its own implementation
	 * @param snake
	 */
	protected abstract void intersectionHandling(Snake snake);

	/**
	 * Checks if the specified snake collects this item and if so, the item effect gets activated
	 * @param snake
	 */
	public void checkSnakeIntersection(Snake snake) {
		if(!effectActivated) {
			HeadTile head = snake.getHead();
			if(IntersectionUtils.isIntersectingRectangleCircle(middleX, middleY, head.getX(), head.getY(), LENGTH, LENGTH, SnakeTile.RADIUS)) {
				effectActivated = true;
				intersectionHandling(snake);
			}
		}
	}
	/**
	 * Loads the image by the specified file name and returns it
	 * @param iconName The filename of the icon image
	 * @return a {@code BufferedImage} containing the information of the icon image
	 */
	private BufferedImage loadIcon(String iconName) {
		try {
			return ImageIO.read(new File(System.getProperty("user.dir") + "/src/main/resources/items/" + iconName + ".png"));
		} catch (IOException e) {
			throw new RuntimeException("Could not find the icon called: " + iconName);
		}
	}
	/**
	 * Renders this item to the frame
	 * @param g The graphics object to draw with
	 */
	public void render(Graphics g) {
		g.drawImage(icon, drawX, drawY, LENGTH, LENGTH, null);
	}
	
	public double getX() {
		return middleX;
	}
	
	public double getY() {
		return middleY;
	}
	
	public boolean isEffectActivated() {
		return effectActivated;
	}
	
	public String toString() {
		return getClass().getSimpleName() + " Item middle point: [" + middleX + ", " + middleY + "]";
	}

    public void disable() {
		disabled = true;
    }
}
