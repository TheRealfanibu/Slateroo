package logic;

import java.awt.Color;
import java.awt.Graphics;

import gui.Frame;
import items.TeleportBorderItem;

/**
 * Represents the arena and the border in the game
 * @author Jonas
 *
 */
public class Arena {
	/**
	 * The gap between the border of the frame and the border of the arena in pixels.
	 */
	private static final int GAP = 40;
	/**
	 * The width of the arena border in pixels
	 */
	private static final int BORDER_WIDTH = SnakeTile.RADIUS * 2;
	/**
	 * Both the x and y position where the border transitions into the arena in pixels.
	 */
	public static final int BORDER_HITBOX = GAP + BORDER_WIDTH;
	/**
	 * The width of the arena in pixels
	 */
	public static final int WIDTH = Frame.RIGHT_FRAME_BORDER_X - BORDER_HITBOX * 2;
	/**
	 * The height of the arena in pixels
	 */
	public static final int HEIGHT = Frame.LOWER_FRAME_BORDER_Y - BORDER_HITBOX * 2;
	/**
	 * The distance between the upper left corner and the lower right corner of the arena
	 */
	public static final double MAX_DISTANCE = Math.sqrt(WIDTH * WIDTH + HEIGHT * HEIGHT); // pythagorean theorem
	/**
	 * The draw width of the horizontal border in pixels.
	 */
	private static final int BORDER_DRAW_WIDTH = WIDTH + BORDER_WIDTH * 2;
	/**
	 * The x-coordinate of the hitbox of the right border
	 */
	private static final int RIGHT_BORDER_HITBOX_X = Frame.RIGHT_FRAME_BORDER_X - BORDER_HITBOX;
	/**
	 * the y-coordinate of the hitbox of the lower border
	 */
	private static final int LOWER_BORDER_HITBOX_Y = Frame.LOWER_FRAME_BORDER_Y - BORDER_HITBOX;
	/**
	 * The default border color
	 */
	private static final Color NORMAL_BORDER_COLOR = Color.YELLOW;
	/**
	 * The color the border has, when it is in teleport mode. Teleport mode can be activated by collecting an {@link TeleportBorderItem}
	 */
	private static final Color TELEPORT_BORDER_COLOR = Color.GREEN;
	/**
	 * The background color of the arena
	 */
	private static final Color ARENA_COLOR = new Color(210,105,30);
	/**
	 * Indicates whether the arena is in teleport mode which can be activated by collecting an {@link TeleportBorderItem}
	 */
	private boolean teleportMode = false;
	/**
	 * Gives the reference of itself to objects who need access to this class.
	 */
	public Arena() {
		TeleportBorderItem.setArena(this);
	}
	
	/**
	 * Checks if the specified head of a snake is colliding with the border of the arena
	 * @param head The head of the snake that needs to be checked
	 * @return true, if the snake collides with the border, false otherwise
	 */
	public boolean isSnakeCollidingWithBorder(HeadTile head) {
		return head.getXMinusRadius() <= 0 || // left
				head.getXPlusRadius() >= RIGHT_BORDER_HITBOX_X - BORDER_HITBOX || // right
				head.getYMinusRadius() <= 0 || // up
				head.getYPlusRadius() >= LOWER_BORDER_HITBOX_Y - BORDER_HITBOX;
	}

	/**
	 * Renders the border to the frame
	 * @param g The graphics object to draw with
	 */
	public void renderBorder(Graphics g) {
		g.setColor(teleportMode ? TELEPORT_BORDER_COLOR : NORMAL_BORDER_COLOR);
		
		g.fillRect(GAP, GAP, BORDER_DRAW_WIDTH, BORDER_WIDTH); // upper horizontal
		g.fillRect(GAP, HEIGHT + BORDER_HITBOX, BORDER_DRAW_WIDTH, BORDER_WIDTH); // lower horizontal
		g.fillRect(GAP, BORDER_HITBOX, BORDER_WIDTH, HEIGHT); // left vertical
		g.fillRect(BORDER_HITBOX + WIDTH, BORDER_HITBOX, BORDER_WIDTH, HEIGHT); // right vertical
	}
	/**
	 * Renders the background of the arena to the frame
	 * @param g The graphics object to draw with
	 */
	public void renderArena(Graphics g) {
		g.setColor(ARENA_COLOR);
		g.fillRect(BORDER_HITBOX, BORDER_HITBOX, WIDTH, HEIGHT);
	}
	
	public void setTeleportMode(boolean mode) {
		teleportMode = mode;
	}
	
	public boolean isInTeleportMode() {
		return teleportMode;
	}
}
