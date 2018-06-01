package logic;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import gui.Frame;

public class SnakeTile implements GameObject, Serializable{
	public static final int RADIUS = Frame.toScaledPixelSize(20);
	
	protected double x, y; // coords of the middlepoint of the circle, x = 0 and y = 0 is drawn at the left upper corner of the arena
	
	private Color color;
	
	protected double moveAngle; // the angle of the last direction the tile moved -> used for HeadToTailItem
	
	public SnakeTile(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void move(double dx, double dy) {
		x += dx;
		y += dy;
	}
	
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
	
	private int[] calcDrawCoordinates(double dimensionPos, int dimensionArenaLength) {	
		double borderGap = getTranslatedPos(dimensionPos, dimensionArenaLength);
		int iBorderGap = (int) borderGap;
		
		if(borderGap >= SnakeTile.RADIUS && borderGap <= dimensionArenaLength - SnakeTile.RADIUS)
			return retranslateBorderHitbox(new int[] {iBorderGap});
		else if(borderGap < SnakeTile.RADIUS)
			return retranslateBorderHitbox(new int[] {iBorderGap, iBorderGap + dimensionArenaLength});
		else // borderGap <= dimensionArenaLength - SnakeTile.RADIUS
			return retranslateBorderHitbox(new int[] {iBorderGap, iBorderGap - dimensionArenaLength});
	}
	
	private int[] retranslateBorderHitbox(int[] toTranslate) {
		for(int i = 0; i < toTranslate.length; i++) {
			toTranslate[i] = toDrawCoords(toTranslate[i]);
		}
		return toTranslate;
	}
	
	private int toDrawCoords(int pos) {
		return pos + Arena.BORDER_HITBOX;
	}
	
	private double getTranslatedPos(double pos, int dimLength) {
		double translated = pos % dimLength; // squashs it into the arena
		if(translated > 0)
			return translated;
		return dimLength + translated;
	}

	public double getMoveAngle() {
		moveAngle %= 360;
		if(moveAngle < 0)
			return moveAngle + 360;
		return moveAngle;
	}

	public double getX() {
		return getTranslatedPos(x, Arena.WIDTH);
	}

	public double getY() {
		return getTranslatedPos(y, Arena.HEIGHT);
	}
	
	public double getTeleportX() {
		return x;
	}
	
	public double getTeleportY() {
		return y;
	}
	
	public void setTeleportX(double x) {
		this.x = x;
	}
	
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
