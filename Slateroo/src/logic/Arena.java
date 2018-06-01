package logic;

import java.awt.Color;
import java.awt.Graphics;

import gui.Frame;
import items.TeleportBorderItem;

public class Arena {
	private static final int GAP = 40;
	private static final int BORDER_WIDTH = SnakeTile.RADIUS * 2;
	
	public static final int BORDER_HITBOX = GAP + BORDER_WIDTH;
	public static final int WIDTH = Frame.RIGHT_FRAME_BORDER_X - BORDER_HITBOX * 2;
	public static final int HEIGHT = Frame.LOWER_FRAME_BORDER_Y - BORDER_HITBOX * 2;
	public static final double MAX_DISTANCE = Math.sqrt(WIDTH * WIDTH + HEIGHT * HEIGHT);
	
	private static final int BORDER_DRAW_WIDTH = WIDTH + BORDER_WIDTH * 2;
	
	private static final int RIGHT_BORDER_HITBOX_X = Frame.RIGHT_FRAME_BORDER_X - BORDER_HITBOX;
	private static final int LOWER_BORDER_HITBOX_Y = Frame.LOWER_FRAME_BORDER_Y - BORDER_HITBOX;
	
	private static final Color NORMAL_BORDER_COLOR = Color.YELLOW;
	private static final Color TELEPORT_BORDER_COLOR = Color.GREEN;
	
	private static final Color ARENA_COLOR = new Color(210,105,30); // schokoladenbraun
	
	private boolean teleportMode = false;
	
	public Arena() {
		TeleportBorderItem.setArena(this);
		Snake.setArena(this);
	}
	
	public boolean isSnakeColliding(HeadTile head) {	
		if(head.getXMinusRadius() <= 0 || // left
				head.getXPlusRadius() >= RIGHT_BORDER_HITBOX_X - BORDER_HITBOX || // right
				head.getYMinusRadius() <= 0 || // up
				head.getYPlusRadius() >= LOWER_BORDER_HITBOX_Y - BORDER_HITBOX) // down
			return true;
		return false;
	}

	
	public void renderBorder(Graphics g) {
		g.setColor(teleportMode ? TELEPORT_BORDER_COLOR : NORMAL_BORDER_COLOR);
		
		g.fillRect(GAP, GAP, BORDER_DRAW_WIDTH, BORDER_WIDTH); // upper horizontal
		g.fillRect(GAP, HEIGHT + BORDER_HITBOX, BORDER_DRAW_WIDTH, BORDER_WIDTH); // lower horizontal
		g.fillRect(GAP, BORDER_HITBOX, BORDER_WIDTH, HEIGHT); // left vertical
		g.fillRect(BORDER_HITBOX + WIDTH, BORDER_HITBOX, BORDER_WIDTH, HEIGHT); // right vertical
	}
	
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
