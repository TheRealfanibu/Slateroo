package gui;

import java.awt.Toolkit;

import javax.swing.JFrame;

import render.GamePanel;

public class Frame extends JFrame{
	private static final int TEST_SCREEN_WIDTH = 2560;
	
	public static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public static final int WIDTH = SCREEN_WIDTH;
	public static final int HEIGHT = Frame.toScaledPixelSize(1000);
	
	public static final int WIDTH_REDUCE = 6; // right frame border + left frame border are 6px thick 
	public static final int HEIGHT_REDUCE = 29; // upper frame border + lower frame border are 29px thick
	
	public static final int RIGHT_FRAME_BORDER_X = WIDTH - WIDTH_REDUCE;
	public static final int LOWER_FRAME_BORDER_Y = HEIGHT - HEIGHT_REDUCE;
	
	private GamePanel gamePanel;
	
	public Frame(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
		
		addComponents();
		initFrame();
	}
	
	private void addComponents() {
		add(gamePanel);
	}
	
	private void initFrame() {
		setTitle("Slateroo");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setLocation(SCREEN_WIDTH - WIDTH, 0);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static int toScaledPixelSize(int size) {
		return size * SCREEN_WIDTH / TEST_SCREEN_WIDTH; // only works well if monitor has 16:9 format
	}
}
