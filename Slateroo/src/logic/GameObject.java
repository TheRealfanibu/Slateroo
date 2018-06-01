package logic;

import java.awt.Graphics;

public interface GameObject {
	double getX();
	double getY();
	
	void render(Graphics g);
}
