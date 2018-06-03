package logic;

import java.awt.Graphics;
import java.io.Serializable;

import gui.Frame;
import io.Direction;
import items.SlowItem;
import items.SpeedItem;
import items.ZickZackMovementItem;

public class HeadTile extends SnakeTile implements Serializable{
	/**
	 * The default velocity the head of a snake moves measured in pixels per second
	 */
	public static final int VELOCITY = Frame.toScaledPixelSize(400);
	/**
	 * The default distance the head of a snake moves per frame
	 */
	public static final double D_VELOCITY = (double) VELOCITY / Main.FPS;
	/**
	 * The default angle the head of a snake rotates in the circle when moving to the left or right, measured in degrees per second
	 */
	private static final int ROTATION_ANGLE = Frame.toScaledPixelSize(320);
	/**
	 * The default angle the head of a snake rotates in the circle when moving to the left or right, measured in degrees
	 */
	private static final double D_ROTATION_ANGLE = (double) ROTATION_ANGLE / Main.FPS;
	/**
	 * The default radius of the circle on which the snake is rotating on when moving to the left or right
	 */
	private static final double CIRCLE_RADIUS = VELOCITY * 180 / (Math.PI * ROTATION_ANGLE);
	/**
	 * When the snake moves to the right or left, the head rotates in a circle. This variable is storing the angle of rotation in degrees.
	 * Starting with "0" degrees at the top of the circle and increasing up to "360" degrees clockwise. This angle is storing the position
	 * on the circle when moving to the right. For example if the snake moves straight to the right, this angle will be "0" degrees.
	 * This is because if the snake moves to the right, the head will rotate clockwise on the circle from this "0" degrees position.
	 * If the snake moves left, then "180" degrees will
	 * be added to this angle and the head will rotate anticlockwise from this "180" degrees position on the circle.
	 */
	private double rotationAngle;
	/**
	 * The distance the snake moves in x-direction when moving straight.
	 */
	private double dx;
	/**
	 * The distance the snake moves in y-direction when moving straight.
	 */
	private double dy;
	/**
	 * The current velocity, measured in pixels per frame.
	 * As default, this is set to {@code D_VELOCITY}
	 */
	private double dVelocity;
	/**
	 * The current velocity, measured in pixels per frame.
	 * As default, this is set to {@code D_VELOCITY}
	 */
	private double dRotationAngle;
	/**
	 * The current radius of the circle on which the snake is rotating on when moving to the left or right
	 */
	private double circleRadius;
	/**
	 * The previous move-direction
	 */
	private Direction previousDir = Direction.MIDDLE;
	/**
	 * Indicates whether the speed has changed because of a {@link SpeedItem} or a {@link SlowItem} since the last movement
	 */
	private boolean velocityChanged = false;
	/**
	 * Indicates whether the snake is in a Zick Zack movement mode, caused by {@link ZickZackMovementItem}
	 */
	private boolean zickZackMode = false;
	
	public HeadTile(int x, int y, boolean startPosOnTheRight) {
		super(x, y);
	
		init(startPosOnTheRight);
	}
	/**
	 * Alternative movement style when the zick zack movement mode is active, caused by collecting an {@link ZickZackMovementItem}
	 * @param dir The direction which the player or AI decided to move
	 */
	private void zickZackMove(Direction dir) {
		if(dir == previousDir || dir == Direction.MIDDLE || dir == Direction.LEFT_RIGHT) // doesn't move when the key is hold down, but only when you
			return;																		 // press it again
		else if(dir == Direction.LEFT)
			moveAngle -= ZickZackMovementItem.ZICK_ZACK_ANGLE_CHANGE;
		else // dir == Direction.RIGHT
			moveAngle += ZickZackMovementItem.ZICK_ZACK_ANGLE_CHANGE;
		
		calcStraightMovement();
	}
	
	/**
	 * The default movement of the head of the snake. Moving straight if either pressing no keys or pressing both left and right direction keys.
	 * Rotating in a circle when moving to the right or left.
	 * @param dir
	 */
	public void move(Direction dir) {
		if(zickZackMode) 
			zickZackMove(dir);
		else if(dir.equals(Direction.LEFT) || dir.equals(Direction.RIGHT)) {
			double radAngle, radNewAngle;
			if(dir.equals(Direction.LEFT)) {
				double correctedAngle = rotationAngle + 180; 
				
				radAngle = Math.toRadians(correctedAngle);
				radNewAngle = Math.toRadians(correctedAngle - dRotationAngle);
				
				rotationAngle -= dRotationAngle;		
			}
			else {
				radAngle = Math.toRadians(rotationAngle);			
				radNewAngle = Math.toRadians(rotationAngle + dRotationAngle);
				
				rotationAngle += dRotationAngle;
			}
			double old_x = Math.sin(radAngle);   // not scaled to circleRadius
			double old_y = Math.cos(radAngle);
			double new_x = Math.sin(radNewAngle);
			double new_y = Math.cos(radNewAngle);
			
			dx = (new_x - old_x) * circleRadius; // scaled to circleRadius
			dy = (old_y - new_y) * circleRadius; // old - new because of the coordinate system
			
			moveAngle = rotationAngle + 90; // This is generally only true for right movement, because it should be -90 for
											// left movement, but since the rotationAngle for left movement is rotationAngle+180 this always true
		}
		else if(previousDir.equals(Direction.LEFT) || previousDir.equals(Direction.RIGHT) || velocityChanged) // Direction.MIDDLE || Direction.LEFT_RIGHT
				calcStraightMovement();
		x += dx;
		y += dy;
		previousDir = dir;
	}
	
	/**
	 * Calculates the straight movement based on the {@code moveAngle}
	 */
	private void calcStraightMovement() {
		dx = Math.sin(Math.toRadians(moveAngle)) * dVelocity;
		dy = - Math.cos(Math.toRadians(moveAngle)) * dVelocity; // minus because of coordinate system
		velocityChanged = false;
	}
	
	public void changeSpeed(double speedAmount) {
		double changeFactor = (speedAmount / (dVelocity * Main.FPS)) + 1;
		dVelocity *= changeFactor;
		dRotationAngle *= changeFactor;
		velocityChanged = true;
	}
	

	public void changeSteerAngle(double angleAmount) {
		double changeFactor = (angleAmount / (dRotationAngle * Main.FPS)) + 1;
		dRotationAngle *= changeFactor;
		circleRadius /= changeFactor;
	}

	
	private void init(boolean startPosOnTheRight) {
		this.rotationAngle = startPosOnTheRight ? 180 : 0;
		moveAngle = rotationAngle + 90;
		
		dVelocity = D_VELOCITY;
		dRotationAngle = D_ROTATION_ANGLE;
		circleRadius = CIRCLE_RADIUS;
		
		calcStraightMovement();
	}
	
	public void render(Graphics g) {
		super.render(g);
		//eyes.render(g, angle, x, y);
	}
	
	
	public int getXPlusRadius() {
		return (int) getX() + RADIUS;
	}
	public int getXMinusRadius() {
		return (int) getX() - RADIUS;
	}
	public int getYPlusRadius() {
		return (int) getY() + RADIUS;
	}
	public int getYMinusRadius() {
		return (int) getY() - RADIUS;
	}

	public double getDVelocity() {
		return dVelocity;
	}
	
	public void setZickZackMode(boolean mode) {
		zickZackMode = mode;
	}

	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setMoveAngle(double angle) {
		super.setMoveAngle(angle);
		calcStraightMovement();
	}

	public void setCircleAngle(double newCircleAngle) {
		this.rotationAngle = newCircleAngle;
	}
	
	/*straight move overcomplicated xD: 
	 * double slopeToCircleMid = 1 / Math.tan(Math.toRadians(circleAngle)); // 1 / (GK / AK) -> AK / GK -> dy / dx
	double slopeOfNormal = - 1 / slopeToCircleMid; // Die Steigung der Normalen zu der Gerade, die vom Rand des Kreises zum Mittelpunkt führt
	
	boolean movingToTheRight = dx > 0;
	if(Math.abs(dx) > 0.001) {
		dx = Math.sqrt(dVelocity * dVelocity / (1 + slopeOfNormal * slopeOfNormal)); // ist die Tangente an der Stelle des Kreises 
		dx = movingToTheRight ? dx : -dx;													 //	und somit die Bewegungsrichtung
		dy = - slopeOfNormal * dx;
	}
	else {
		dy = dy > 0 ? dVelocity : - dVelocity;
	}*/
}
