package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import game.IntersectionUtils;
import items.manage.ItemEffectMode;
import items.manage.ItemManager;
import items.superClasses.Item;
import items.superClasses.MultiplePlayerEffectItem;
import logic.Arena;
import logic.GameObject;
import logic.HeadTile;
import logic.ObjectSequence;
import logic.Snake;
import logic.SnakeManager;
import logic.SnakeTile;
import mathUtils.MathUtils;

public class ObjectDetector {
	public static final int DIRECTION_AMOUNT = 12;
	private static final double ANGLE_STEP = 360d / DIRECTION_AMOUNT;
	
	private SnakeManager snakeManager;
	private ItemManager itemManager;
	
	public ObjectDetector(SnakeManager snakeManager, ItemManager itemManager) {
		this.snakeManager = snakeManager;
		this.itemManager = itemManager;
	}
	
	public List<Double> calcEnvironmentInputs(Snake snake) {
		List<List<Double>> directionInputs = new LinkedList<>();
		List<Double> borderInputs = new LinkedList<>();
		
		HeadTile head = snake.getHead();
		double headX = head.getX();
		double headY = head.getY();
		double moveAngle = head.getMoveAngle();
		List<GameObject> gameObjects = getAllGameObjectsExceptForOwnTiles(snake);

		for(int i = 0; i < DIRECTION_AMOUNT; i++) {
			double directionAngle = moveAngle + ANGLE_STEP * i;
			Point borderIntersection = calcBorderIntersectionPoints(headX, headY, directionAngle);
            double borderDistance = MathUtils.calcDistance(headX, headY, borderIntersection.getX(), borderIntersection.getY());
            borderInputs.add((Arena.MAX_DISTANCE - borderDistance) / Arena.MAX_DISTANCE);
			//List<GameObject> gameObjectsInDirection = getObjectsInDirection(headX, headY, borderIntersection, gameObjects);
			//Map<Class<?>, NetworkData> nearestDistanceOfClass = calcDistanceOfNearestObjectForEachClass(headX, headY,
			//		gameObjectsInDirection, borderIntersection);
			//directionInputs.add(convertToNetworkInputs(nearestDistanceOfClass));
		}
		//List<Double> networkInputs = directionInputs.stream().flatMap(List::stream).collect(Collectors.toList());
		//return networkInputs;
        return borderInputs;
	}
	
	private List<Double> convertToNetworkInputs(Map<Class<?>, NetworkData> nearestDistanceOfClass) {
		List<Double> inputs = new LinkedList<>();
		for(Class<?> gameClass : ObjectSequence.getGameClassOrder()) {
			NetworkData data = nearestDistanceOfClass.get(gameClass);
			
			double distance = data.getDistance();
			if(data.getDistance() == Double.MAX_VALUE)
				inputs.add(0d);
			else {
				double scaledDistance = (Arena.MAX_DISTANCE - distance) / Arena.MAX_DISTANCE;
				inputs.add(scaledDistance);
			}
			
			ItemEffectMode effectMode = data.getEffectMode();
			if(effectMode != ItemEffectMode.STANDARD) {	
				if(effectMode == ItemEffectMode.UNKNOWN)
					inputs.addAll(Arrays.asList(0d, 0d, 0d));
				else if(effectMode == ItemEffectMode.THIS)
					inputs.addAll(Arrays.asList(1d,0d,0d));
				else if(effectMode == ItemEffectMode.EVERY)
					inputs.addAll(Arrays.asList(0d,1d,0d));
				else // ItemEffectMode.EXCEPT
					inputs.addAll(Arrays.asList(0d,0d,1d));
			}
		}
		return inputs;
	}
	
	private Map<Class<?>, NetworkData> calcDistanceOfNearestObjectForEachClass(double headX, double headY, 
			List<GameObject> gameObjectsInDirection, Point borderIntersection) {
		Map<Class<?>, NetworkData> nearestObjectOfClass = new HashMap<>();
		for(GameObject object : gameObjectsInDirection) {
			ItemEffectMode itemMode = ItemEffectMode.STANDARD;
			if(MultiplePlayerEffectItem.class.isAssignableFrom(object.getClass()))
				itemMode = ((MultiplePlayerEffectItem) object).getEffectMode();
			
			double distance = MathUtils.calcDistance(headX, headY, object.getX(), object.getY());
			NetworkData data = new NetworkData(distance, itemMode);
			nearestObjectOfClass.merge(object.getClass(), data,
					(currentData, newData) -> newData.getDistance() < currentData.getDistance() ? newData : currentData);
		}
		for(Class<?> clazz : ObjectSequence.getGameClassOrder()) {
			ItemEffectMode effectMode = MultiplePlayerEffectItem.class.isAssignableFrom(clazz) ? ItemEffectMode.UNKNOWN : ItemEffectMode.STANDARD;
			nearestObjectOfClass.putIfAbsent(clazz, new NetworkData(Double.MAX_VALUE, effectMode));
		}
		double borderDistance = MathUtils.calcDistance(headX, headY, borderIntersection.getX(), borderIntersection.getY());
		nearestObjectOfClass.put(Arena.class, new NetworkData(borderDistance, ItemEffectMode.STANDARD));
		return nearestObjectOfClass;
	}

	private List<GameObject> getObjectsInDirection(double headX, double headY, Point borderEndPoint, List<GameObject> gameObjects) {
		List<GameObject> intersectingGameObjects = new ArrayList<>();
		for(GameObject object : gameObjects) {
			if(object instanceof Item) {
				if(IntersectionUtils.isLineIntersectingRectangle(headX, headY, borderEndPoint.getX(), borderEndPoint.getY(),
						object.getX(), object.getY(), Item.LENGTH, Item.LENGTH)) 
					intersectingGameObjects.add(object);
			}
			else if(IntersectionUtils.isLineIntersectingCircle(headX, headY, borderEndPoint.getX(), borderEndPoint.getY(),
					object.getX(), object.getY(), SnakeTile.RADIUS))
				intersectingGameObjects.add(object);
		}
		return intersectingGameObjects;
	}
	
	private Point calcBorderIntersectionPoints(double headX, double headY, double directionAngle) {
		int iHeadX = (int) headX;
		double m = 1 / Math.tan(Math.toRadians(directionAngle)); // slope of line
		double b = -headY; // y-axis on the head of the snake
		
		directionAngle %= 360;
		if(directionAngle < 90)
			return calcIntersectionPointOnBorder(m, b, iHeadX, 0, Arena.WIDTH); // Cartesian Coordinate System upper border -> 0 lower border -> - Arena.HEIGHT
		else if(directionAngle < 180)
			return calcIntersectionPointOnBorder(m, b, iHeadX, Arena.HEIGHT, Arena.WIDTH);
		else if(directionAngle < 270)
			return calcIntersectionPointOnBorder(m, b, iHeadX, Arena.HEIGHT, 0);
		else
			return calcIntersectionPointOnBorder(m, b, iHeadX, 0, 0);
	}
	
	private Point calcIntersectionPointOnBorder(double m, double b, int headX, int horizontalBorderY, int verticalBorderX) {
		int horizontalBorderIntersectionX = calcHorizontalBorderIntersection(m, b, -horizontalBorderY, headX);
		if(verticalBorderX == 0 && horizontalBorderIntersectionX >= 0||
				verticalBorderX == Arena.WIDTH && horizontalBorderIntersectionX <= Arena.WIDTH && horizontalBorderIntersectionX > 0) {
			return new Point(horizontalBorderIntersectionX, horizontalBorderY);
		} else {
			int verticalBorderIntersectionY = calcVerticalBorderIntersection(m, b, verticalBorderX, headX);
			return new Point(verticalBorderX, verticalBorderIntersectionY);
		}
	}
	
	private int calcHorizontalBorderIntersection(double m, double b, int horizontalBorderY, int headX) { // calc Intersection point with y-value
		int intersectionX = (int) ((horizontalBorderY - b) / m);
		return intersectionX + headX;
	}
	
	private int calcVerticalBorderIntersection(double m, double b, int verticalBorderX, int headX) {// get y value from intersection point
		return (int) Math.abs(m * (verticalBorderX - headX) + b);
	}
	
	private List<GameObject> getAllGameObjectsExceptForOwnTiles(Snake snake) {
		List<GameObject> gameObjects = new ArrayList<>();
		List<SnakeTile> opponentSnakeTiles = snakeManager.getSnakesExceptThisSnake(snake).stream()
				.map(Snake::getTiles)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		List<Item> items = itemManager.getItems();
		gameObjects.addAll(opponentSnakeTiles);
		gameObjects.addAll(items);
		return gameObjects;
		
	}

	public List<Point> calcDirectionLines(Snake snake) {
		List<Point> endPoints = new ArrayList<>();
		HeadTile head = snake.getHead();
		for(int i = 0; i < DIRECTION_AMOUNT; i++) {
			double directionAngle = head.getMoveAngle() + ANGLE_STEP * i;
			endPoints.add(calcBorderIntersectionPoints(head.getX(), head.getY(), directionAngle));
		}
		return endPoints;
	}
	
	public List<Point> calcIntersectingObjectsLine(Snake snake) {
		List<Point> connectionPoints = new ArrayList<>();
		
		HeadTile head = snake.getHead();
		double headX = head.getX();
		double headY = head.getY();
		double moveAngle = head.getMoveAngle();
		List<GameObject> gameObjects = getAllGameObjectsExceptForOwnTiles(snake);
		for(int i = 0; i < DIRECTION_AMOUNT; i++) {
			double directionAngle = moveAngle + ANGLE_STEP * i;
			Point borderIntersection = calcBorderIntersectionPoints(headX, headY, directionAngle);
			List<GameObject> gameObjectsInDirection = getObjectsInDirection(headX, headY, borderIntersection, gameObjects);
			for(GameObject object : gameObjectsInDirection) {
				connectionPoints.add(new Point((int) object.getX(), (int) object.getY()));
			}
		}
		return connectionPoints;
	}
	
	private class NetworkData {
		private double distance; // distance to player
		private ItemEffectMode effectMode;
		
		public NetworkData(double distance, ItemEffectMode effectMode) {
			this.distance = distance;
			this.effectMode = effectMode;
		}

		public double getDistance() {
			return distance;
		}

		public ItemEffectMode getEffectMode() {
			return effectMode;
		}
		
		public String toString() {
			return "Distance: " + distance + " EffectMode: " + effectMode;
		}
	}
	
	
}
