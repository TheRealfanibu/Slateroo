package image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

	public static BufferedImage loadImage(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static void writeImage(BufferedImage img, String format, String path) {
		try {
			ImageIO.write(img, format, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BufferedImage scaleImage(BufferedImage src, int width, int height) {
		Image scaledImg = src.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
		BufferedImage scaledBI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = scaledBI.getGraphics();
		g.drawImage(scaledImg, 0, 0, null);
		g.dispose();
		return scaledBI;
	}
	
	public static boolean imgEquals(BufferedImage imgA, BufferedImage imgB) {
		if(imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
			for(int i = 0; i < imgA.getWidth(); i++) {
				for(int j = 0; j < imgA.getHeight(); j++) {
					if(imgA.getRGB(i, j) != imgB.getRGB(i, j))
						return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean imgContainsPixel(BufferedImage img, BufferedImage pix) {
		if(pix.getWidth() == 1 && pix.getHeight() == 1) {
			for(int i = 0; i < img.getWidth(); i++) {
				for(int j = 0; j < img.getHeight(); j++) {
					if(img.getRGB(i, j) == pix.getRGB(0, 0))
						return true;
				}
			}
		}
		return false;
	}
}
