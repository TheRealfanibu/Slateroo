package logic;

import java.awt.Graphics;
import java.io.Serializable;

import gui.Frame;
import io.Direction;
import items.ZickZackMovementItem;

public class HeadTile extends SnakeTile implements Serializable{
	public static final int VELOCITY = Frame.toScaledPixelSize(400); //  default velocity in pixels per second
	public static final double D_VELOCITY = (double) VELOCITY / Main.FPS;
	
	private static final int ANGLE = Frame.toScaledPixelSize(320); // default angle the snakes rotates per second in degrees
	private static final double D_ANGLE = (double) ANGLE / Main.FPS; // angle, which the snake moves per frame in radians when circling
	
	private static final double CIRCLE_RADIUS = VELOCITY * 180 / (Math.PI * ANGLE); // default radius of the circle
	
	private double circleAngle;
	private double dx, dy;
	
	private double dVelocity, dAngle;
	private double circleRadius;
	
	private Direction previousDir = Direction.MIDDLE;
	private boolean velocityChanged = false;
	
	private boolean zickZackMode = false;
	
	public HeadTile(int x, int y, boolean startPosOnTheRight) {
		super(x, y);
	
		init(startPosOnTheRight);
	}
	
	private void zickZackMove(Direction dir) {
		if(dir == previousDir || dir == Direction.MIDDLE || dir == Direction.LEFT_RIGHT)
			return;
		
		if(dir == Direction.LEFT)
			moveAngle -= ZickZackMovementItem.ZICK_ZACK_ANGLE_CHANGE;
		else
			moveAngle += ZickZackMovementItem.ZICK_ZACK_ANGLE_CHANGE;
		
		calcStraightMovement();
	}
	
	public void move(Direction dir) {
		if(zickZackMode) 
			zickZackMove(dir);
			
		else if(dir.equals(Direction.LEFT) || dir.equals(Direction.RIGHT)) { // changing direction and moving in a circle
			double radAngle, radNewAngle;
			if(dir.equals(Direction.LEFT)) {
				double correctedAngle = circleAngle + 180; 
				
				radAngle = Math.toRadians(correctedAngle);
				radNewAngle = Math.toRadians(correctedAngle - dAngle);
				
				circleAngle -= dAngle;		
			}
			else {
				radAngle = Math.toRadians(circleAngle);			
				radNewAngle = Math.toRadians(circleAngle + dAngle);
				
				circleAngle += dAngle;
			}
			double old_x = Math.sin(radAngle);
			double old_y = Math.cos(radAngle);
			double new_x = Math.sin(radNewAngle);
			double new_y = Math.cos(radNewAngle);
			
			dx = (new_x - old_x) * circleRadius; 
			dy = (old_y - new_y) * circleRadius; // old - new because of the coordinate system
			
			moveAngle = circleAngle + 90;
		}
		else if(previousDir.equals(Direction.LEFT) || previousDir.equals(Direction.RIGHT) || velocityChanged) // Direction.MIDDLE || Direction.LEFT_RIGHT
				calcStraightMovement();
		x += dx;
		y += dy;
		previousDir = dir;
	}
	
	private void calcStraightMovement() {
		dx = Math.sin(Math.toRadians(moveAngle)) * dVelocity;
		dy = - Math.cos(Math.toRadians(moveAngle)) * dVelocity;
		velocityChanged = false;
	}
	
	public void changeSpeed(double factor) {
		dVelocity *= factor;
		dAngle *= factor;
		velocityChanged = true;
	}
	

	public void changeSteerAngle(double factor) {
		dAngle *= factor;
		circleRadius /= factor;
	}

	
	private void init(boolean startPosOnTheRight) {
		this.circleAngle = startPosOnTheRight ? 180 : 0;
		moveAngle = circleAngle + 90;
		
		dVelocity = D_VELOCITY;
		dAngle = D_ANGLE;
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
		this.circleAngle = newCircleAngle;
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
