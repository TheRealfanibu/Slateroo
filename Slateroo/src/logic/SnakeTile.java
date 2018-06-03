package logic;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import gui.Frame;
import items.TeleportBorderItem;

/**
 * This class represents one flexible tile of the whole snake
 * @author Jonas
 */
public class SnakeTile implements GameObject, Serializable{
	/**
	 * The radius of a SnakeTile in pixels
	 */
	public static final int RADIUS = Frame.toScaledPixelSize(20);
	/**
	 * The x-coordinate of the middle point of this SnakeTile, x = 0 is drawn at the left border of the Arena
	 * x = Arena.WIDTH is drawn at the right border of the Arena
	 */
	protected double x;
	/**
	 * The y-coordinate of the middle point of this SnakeTile, y = 0 is drawn at the upper border of the Arena
	 * x = Arena.HEIGHT is drawn at the lower border of the Arena
	 */
	protected double y;
	/**
	 * The specified color of this SnakeTile
	 */
	private Color color;
	/**
	 * The move angle of the last movement in degrees. Starting with "0" degrees at the top and increasing up to "360" clockwise.
	 */
	protected double moveAngle; 
	/**
	 * Creates an instance of this class with the specified coordinates
	 * @param x The initial x-coordinate
	 * @param y The initial y-coordinate
	 */
	public SnakeTile(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Moves this SnakeTile by the specified x and y amount
	 * @param dx The amount by which this tile should be moved in x direction, a value > 0 means moving to the right.
	 * @param dy The amount by which this tile should be moved in y direction, a value > 0 means moving down.
	 */
	public void move(double dx, double dy) {
		x += dx;
		y += dy;
	}
	
	/**
	 * Renders this tile to the frame
	 * @param g the Graphics object to draw with
	 */
	public void render(Graphics g) {
		g.setColor(color);
		
		int[] drawXs = calcDrawCoordinates(x, Arena.WIDTH);
		int[] drawYs = calcDrawCoordinates(y, Arena.HEIGHT);
		
		for(int drawX : drawXs) {
			for(int drawY : drawYs) {
				g.fillOval(drawX - SnakeTile.RADIUS, drawY - SnakeTile.RADIUS, SnakeTile.RADIUS * 2, SnakeTile.RADIUS * 2);
			}
		}

	}
	/**
	 * Calculates all the draw coordinates. There may be multiple ones for one tile because of {@link TeleportBorderItem}
	 * @param dimensionPos Either x or y value
	 * @param dimensionArenaLength Either Arena.WIDTH or Arena.HEIGHT
	 * @return The Draw Coordinates for this dimension
	 */
	private int[] calcDrawCoordinates(double dimensionPos, int dimensionArenaLength) {	
		double borderGap = getTranslatedTeleportPos(dimensionPos, dimensionArenaLength);
		int iBorderGap = (int) borderGap;
		
		if(borderGap >= SnakeTile.RADIUS && borderGap <= dimensionArenaLength - SnakeTile.RADIUS)
			return retranslateBorderHitbox(new int[] {iBorderGap});
		else if(borderGap < SnakeTile.RADIUS)
			return retranslateBorderHitbox(new int[] {iBorderGap, iBorderGap + dimensionArenaLength});
		else // borderGap <= dimensionArenaLength - SnakeTile.RADIUS
			return retranslateBorderHitbox(new int[] {iBorderGap, iBorderGap - dimensionArenaLength});
	}
	
	/**
	 * Translates all given coordinates to Draw coordinates. This is needed because x = 0 should not be drawn at the left frame 
	 * but the left Arena Border. The same reason applies for y.
	 * @param toTranslate The coordinates to be translated
	 * @return The translated Coordinates
	 */
	private int[] retranslateBorderHitbox(int[] toTranslate) {
		for(int i = 0; i < toTranslate.length; i++) {
			toTranslate[i] = toDrawCoords(toTranslate[i]);
		}
		return toTranslate;
	}
	/**
	 * Translates the given coordinates to Draw Coordinates. See {@code retranslateBorderHitbox} for explanation
	 * @param pos The Coordinates to be translated
	 * @return The translated Coordinates
	 */
	private int toDrawCoords(int pos) {
		return pos + Arena.BORDER_HITBOX;
	}
	/**
	 * The implementation of the {@link TeleportBorderItem} is that your x-coordinates aren't going to be set to "0"
	 * if you move out of the right border. They will continue to increase. The reason for that is that the other tiles would jump to
	 * you and not follow through the border. Thats why the draw coordinates must be squashed down into the Arena
	 * @param pos The actual x/y coordinate
	 * @param dimLength Width/Height of the Arena
	 * @return The translated Draw Coordinate
	 */
	private double getTranslatedTeleportPos(double pos, int dimLength) {
		double translated = pos % dimLength; // squashs it into the arena 
		if(translated > 0)
			return translated;
		return dimLength + translated;
	}

	/**
	 * @return the move angle of the last movement in the range between "0" and "360" degrees
	 */
	public double getMoveAngle() {
		moveAngle %= 360;
		if(moveAngle < 0)
			return moveAngle + 360;
		return moveAngle;
	}
	
	/**
	 * @return the draw x-coordinates
	 */
	public double getX() {
		return getTranslatedTeleportPos(x, Arena.WIDTH);
	}
	/**
	 * @return the draw y-coordinates
	 */
	public double getY() {
		return getTranslatedTeleportPos(y, Arena.HEIGHT);
	}
	/**
	 * 
	 * @return the real x-coordinates, see {@code getTranslatedTeleportPos()} for explanation
	 */
	public double getTeleportX() {
		return x;
	}
	/**
	 * 
	 * @return the real y-coordinates, see {@code getTranslatedTeleportPos()} for explanation
	 */
	public double getTeleportY() {
		return y;
	}
	/**

	 * @param x The real x-coordinate to be set, see {@code getTranslatedTeleportPos()} for explanation
	 */
	public void setTeleportX(double x) {
		this.x = x;
	}
	/**
	 * @param y The real x-coordinate to be set, see {@code getTranslatedTeleportPos()} for explanation
	 */
	public void setTeleportY(double y) {
		this.y = y;
	}
	
	public void setColor(Color col) {
		color = col;
	}
	
	public void setMoveAngle(double angle) {
		moveAngle = angle;
	}

}
