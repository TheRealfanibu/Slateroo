package io;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;


public class KeyboardSteering extends KeyAdapter implements Steering{
	private Direction dir; // direction of movement of the snake
	
	public KeyboardSteering() {
		dir = Direction.MIDDLE;
	}
	
	public void keyPressed(KeyEvent e) { // m == MIDDLE ; l == LEFT ; r == RIGHT; b == MIDDLE but when both left and right are pressed
		int key = e.getKeyCode();
		if(key == VK_LEFT && dir == Direction.RIGHT ||
				key == VK_RIGHT && dir == Direction.LEFT) {
			dir = Direction.LEFT_RIGHT;
			return;
		}
		
		if(dir != Direction.MIDDLE) // prevents repeated signal from pressed key
			return;
		else if(key == VK_LEFT)
			dir = Direction.LEFT;
		else if(key == VK_RIGHT) 
			dir = Direction.RIGHT;
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(dir == Direction.LEFT_RIGHT) {
			if(key == VK_LEFT)
				dir = Direction.RIGHT;
			else if(key == VK_RIGHT)
				dir = Direction.LEFT;
		}
		else
			dir = Direction.MIDDLE;
	}
	
	public Direction getMoveDirection() {
		return dir;
	}
	
	public boolean isAISteering() {
		return false;
	}
}
