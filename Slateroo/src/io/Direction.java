package io;

public enum Direction {
	MIDDLE, LEFT, RIGHT, LEFT_RIGHT;
	
	public Direction reverse() {
		if(this == LEFT)
			return RIGHT;
		else if(this == RIGHT)
			return LEFT;
		else
			return MIDDLE;
	}
}
