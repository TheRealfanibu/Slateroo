package render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import javax.swing.JPanel;

import ai.ObjectDetector;
import game.FPSCounter;
import gui.Frame;
import items.TeleportBorderItem;
import items.manage.ItemManager;
import logic.Arena;
import logic.HeadTile;
import logic.SnakeManager;
import mathUtils.MathUtils;

public class GamePanel extends JPanel{
	private static final Color BG = Color.CYAN.darker().darker();
	
	private SnakeManager snakeManager;
	private ItemManager itemManager;
	private ObjectDetector objectDetector;
	private Arena arena;
	
	private FPSCounter fps = new FPSCounter("render", 5);
	
	public GamePanel(SnakeManager snakeManager, ItemManager itemManager, ObjectDetector objectDetector, Arena arena) {
		this.snakeManager = snakeManager;
		this.itemManager = itemManager;
		this.objectDetector = objectDetector;
		this.arena = arena;
		
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(Frame.WIDTH, Frame.HEIGHT));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		fps.count();
		
		g.setColor(BG);
		g.fillRect(0, 0, Frame.WIDTH, Frame.HEIGHT);
		
		arena.renderArena(g);
		itemManager.render(g);
		snakeManager.render(g);
		arena.renderBorder(g);
		//drawObjectDetection(g);
	}
	
	private void drawObjectDetection(Graphics g) {
		snakeManager.getSnakes().forEach(snake -> {
			HeadTile head = snake.getHead();
			List<Point> points = objectDetector.calcDirectionLines(snake);
			List<Point> objectPoints = objectDetector.calcIntersectingObjectsLine(snake);
			g.setColor(Color.BLUE);
			points.forEach(endPoint -> {
				double distance = MathUtils.calcDistance(head.getX(), head.getY(), endPoint.getX(), endPoint.getY());
				double brightness = (Arena.MAX_DISTANCE - distance) / Arena.MAX_DISTANCE;
				g.setColor(new Color((int) (255 * brightness),(int) (255 * brightness),(int) (255 * brightness)));
				g.drawLine((int) head.getX() + Arena.BORDER_HITBOX, (int) head.getY() + Arena.BORDER_HITBOX,
						(int) endPoint.getX() + Arena.BORDER_HITBOX, (int) endPoint.getY() + Arena.BORDER_HITBOX);
			});
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(2));
			objectPoints.forEach(intersectPoint -> {
				double distance = MathUtils.calcDistance(head.getX(), head.getY(), intersectPoint.getX(), intersectPoint.getY());
				double brightness = (Arena.MAX_DISTANCE - distance) / Arena.MAX_DISTANCE;
				g2.setColor(new Color((int) (255 * brightness),0,0));
				g2.drawLine((int) head.getX() + Arena.BORDER_HITBOX, (int) head.getY() + Arena.BORDER_HITBOX,
						(int) intersectPoint.getX() + Arena.BORDER_HITBOX, (int) intersectPoint.getY() + Arena.BORDER_HITBOX);
			});
			g2.setStroke(new BasicStroke(1));
		});
	}
}
