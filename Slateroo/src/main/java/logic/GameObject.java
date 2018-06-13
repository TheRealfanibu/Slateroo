package logic;

import java.awt.Graphics;

import ai.EnvironmentInfo;
import items.superClasses.Item;
/**
 * Every game class, such as {@link SnakeTile}s and {@link Item}s are subclasses from this class.
 * This class is only needed to generalize properties of game objects for the AI input, see {@link EnvironmentInfo}.
 * @author Jonas
 *
 */
public interface GameObject {
	double getX();
	double getY();
	
	void render(Graphics g);
}
