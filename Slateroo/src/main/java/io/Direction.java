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
	
	public static Direction ofIndex(int index) {
		switch(index) {
		case 0: return MIDDLE;
		case 1: return LEFT;
		case 2: return RIGHT;
		}
		throw new IllegalArgumentException("The index to convert to a Direction object must be in the range between 0 and 2: " + index);
	}
	
}
