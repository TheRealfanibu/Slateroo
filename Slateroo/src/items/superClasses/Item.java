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

public abstract class Item implements GameObject{
	public static final int LENGTH = 50;
	private static final int LENGTH_HALF = LENGTH / 2;

	protected static SnakeManager snakeManager;
			
	protected final BufferedImage icon;
	
	protected final int leftX, topY, drawX, drawY; // left upper corner
	private final double middleX, middleY;
	
	protected boolean effectActivated = false;
	
	public Item(int middleX, int middleY, String iconName) {
		this.middleX = middleX;
		this.middleY = middleY;
		
		leftX = middleX - LENGTH_HALF;
		topY = middleY - LENGTH_HALF;
		drawX = leftX + Arena.BORDER_HITBOX;
		drawY = topY + Arena.BORDER_HITBOX;
		
		icon = loadIcon(iconName);
	}
	
	protected abstract void intersectionHandling(Snake snake);
	
	public static double toMiddlePos(double pos) {
		return pos + LENGTH_HALF;
	}

	public List<Snake> checkSnakeIntersection(Snake snake) {
		if(!effectActivated) {
			HeadTile head = snake.getHead();
			if(IntersectionUtils.isIntersectingRectangleCircle(middleX, middleY, head.getX(), head.getY(), LENGTH, LENGTH, SnakeTile.RADIUS)) {
				effectActivated = true;
				intersectionHandling(snake);
			}
		}
		return null;
	}
	
	private BufferedImage loadIcon(String iconName) {
		try {
			return ImageIO.read(new File("res/items/" + iconName + ".png"));
		} catch (IOException e) {
			throw new RuntimeException("Could not find the icon called: " + iconName);
		}
	}
	
	public void render(Graphics g) {
		if(!effectActivated)
			g.drawImage(icon, drawX, drawY, LENGTH, LENGTH, null);
	}

	public static void setSnakeManager(SnakeManager manager) {
		snakeManager = manager;
	}
	
	public boolean isEffectActivated() {
		return effectActivated;
	}
	
	public double getX() {
		return middleX;
	}
	
	public double getY() {
		return middleY;
	}
	
	public String toString() {
		return getClass().getSimpleName() + " Item: [" + leftX + ", " + topY + "]";
	}
	
}
