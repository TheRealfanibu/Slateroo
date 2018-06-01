package render;

import java.awt.Color;
import java.awt.Graphics;

import logic.SnakeTile;

public class SnakeEyes {
	private static final int ANGLE = 45;
	private static final double POSITION = 0.5; // 0 = in the middle of the circle ; 1 = on the border of the circle
	
	private static final int PUPILLE_RADIUS = 2;
	private static final int EYES_RADIUS = 4;
	
	public void render(Graphics g, double steerAngle, double headX, double headY) {
		double leftEyeAngle = steerAngle - ANGLE;
		double rightEyeAngle = steerAngle + ANGLE;
		
		double leftEyeDX = Math.sin(leftEyeAngle) * SnakeTile.RADIUS * POSITION;
		double leftEyeDY = Math.cos(leftEyeAngle) * SnakeTile.RADIUS * POSITION;
		double rightEyeDX = Math.sin(rightEyeAngle) * SnakeTile.RADIUS * POSITION;
		double rightEyeDY = Math.cos(rightEyeAngle) * SnakeTile.RADIUS * POSITION;
		
		double leftEyeX = headX + leftEyeDX;
		double leftEyeY = headY + leftEyeDY;
		double rightEyeX = headX + rightEyeDX;
		double rightEyeY = headY + rightEyeDY;
		
		g.setColor(Color.WHITE);
		g.fillRect((int) (leftEyeX - EYES_RADIUS), (int) (leftEyeY - EYES_RADIUS), EYES_RADIUS * 2, EYES_RADIUS * 2);
		g.fillRect((int) (rightEyeX - EYES_RADIUS), (int) (rightEyeY - EYES_RADIUS), EYES_RADIUS * 2, EYES_RADIUS * 2);
		
		g.setColor(Color.BLACK);
		g.fillRect((int) (leftEyeX - PUPILLE_RADIUS), (int) (leftEyeY - PUPILLE_RADIUS), PUPILLE_RADIUS * 2, PUPILLE_RADIUS * 2);
		g.fillRect((int) (rightEyeX - PUPILLE_RADIUS), (int) (rightEyeY - PUPILLE_RADIUS), PUPILLE_RADIUS * 2, PUPILLE_RADIUS * 2);
	}
	
	
}
