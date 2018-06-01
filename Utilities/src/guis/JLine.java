package guis;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;

public class JLine extends JLabel{
	private static final int CHAR_WIDTH = 8;
	
	public JLine(int width) {
		String text = "";
		for(int i = 0; i < width; i += CHAR_WIDTH) {
			text += "_";
		}
		setText(text);
	}
	
	public static void addLine(int width, Container comp, GridBagConstraints c) {
		JLine line = new JLine(width);
		c.gridy++;
		Insets reset = c.insets;
		c.insets = new Insets(-10,0,0,0);
		int gridwidth = c.gridwidth;
		c.gridwidth = 100;
		comp.add(line, c);
		c.gridwidth = gridwidth;
		c.insets = reset;
	}
}
