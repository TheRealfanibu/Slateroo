package image;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import utilities.Utils;

public class ImagePanel extends JPanel{
	private BufferedImage image;
	
	public ImagePanel(String imagePath, int width, int height) {
		initImage(imagePath, width, height);
	}
	
	private void initImage(String imagePath, int width, int height) {
		image = ImageUtils.loadImage(imagePath);
		image = ImageUtils.scaleImage(image, width, height);
		setPreferredSize(new Dimension(width, height));
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}
	
	public void setImage(String imagePath, int width, int height) {
		initImage(imagePath, width, height);
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
