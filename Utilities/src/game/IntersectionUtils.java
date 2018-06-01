package game;

import mathUtils.MathUtils;

public abstract class IntersectionUtils {
	
	public static boolean isIntersectingRectangleOnlyCoordinates(double x1Left, double y1Top, double x1Right, double y1Bot,
			double x2Left, double y2Top, double x2Right, double y2Bot) {
		
		return (x1Left < x2Right &&
				x1Right > x2Left &&
				y1Top < y2Bot &&
				y1Bot > y2Top);
	}

	public static boolean isIntersectingRectangle(double x1, double y1, double x2, double y2, //coordinates of the center points of the rects
			double width1, double height1, double width2, double height2) {
		double dx = Math.abs(x1 - x2);
		double dy = Math.abs(y1 - y2);
		
		return (dx <= (width1 + width2) / 2 && dy <= (height1 + height2) / 2);
	}
	
	public static boolean isIntersectingCircle(double x1, double y1, double x2, double y2, double rad1, double rad2) {
		double squaredDistance = MathUtils.calcSquaredDistance(x1, y1, x2, y2);
		double maxGap = rad1 + rad2;
		
		return (squaredDistance < maxGap * maxGap);
	}
	
	public static boolean isIntersectingRectangleCircle(double rectX, double rectY, double circleX, double circleY,
			double rectWidth, double rectHeight, double circleRad) {
		double dx = Math.abs(rectX - circleX);
		double dy = Math.abs(rectY - circleY);
		double rectWidthHalf = rectWidth / 2;
		double rectHeightHalf = rectHeight / 2;
		
		double maxGapX = rectWidthHalf + circleRad;
		double maxGapY = rectHeightHalf + circleRad;
		
		if(dx > maxGapX|| dy > maxGapY)
			return false;
		
		if(dx <= maxGapX && dy <= maxGapY)
			return true;
		
		double cornerDistanceSquared = MathUtils.calcSquaredDistance(dx, dy, rectWidthHalf, rectHeightHalf);
		
		return (cornerDistanceSquared <= circleRad * circleRad);
	}
	
	public static boolean isPointInCircle(double pointX, double pointY, double circleX, double circleY, double circleRad) {
		return MathUtils.calcSquaredDistance(pointX, pointY, circleX, circleY) < circleRad * circleRad;
	}
	
	public static boolean isPointInRectangle(double pointX, double pointY, double rectX, double rectY, double rectWidth, double rectHeight) {
		double dx = Math.abs(pointX - rectX);
		double dy = Math.abs(pointY - rectY);
		
		return dx < rectWidth / 2 && dy < rectHeight / 2;
	}
	
	public static boolean isLineIntersectingCircle(double lineStartX, double lineStartY, double lineEndX, double lineEndY,
			double circleX, double circleY, double circleRad) {
		Vector lineVector = new Vector(lineEndX - lineStartX, lineEndY - lineStartY);
		Vector fromLinetoCircleVector = new Vector(circleX - lineStartX, circleY - lineStartY);
		
		double lengthOfProjectedVector = Vector.dot(lineVector, fromLinetoCircleVector) / lineVector.length();
		double squaredDistanceBetweenLineAndCircleCenter = fromLinetoCircleVector.squaredLength()
				- lengthOfProjectedVector * lengthOfProjectedVector;
		return squaredDistanceBetweenLineAndCircleCenter < circleRad * circleRad;
	}
	
	public static boolean isLineIntersectingRectangle(double lineStartX, double lineStartY, double lineEndX, double lineEndY,
			double rectX, double rectY, double rectWidth, double rectHeight) {
		Vector lineVector = new Vector(lineEndX - lineStartX, lineEndY - lineStartY);
		Vector fromLineToCircleVector = new Vector(rectX - lineStartX, rectY - lineStartY);
		
		double lengthOfProjectedVector = Vector.dot(lineVector, fromLineToCircleVector) / lineVector.length();
		Vector scaledProjectedVector = lineVector.scale(lengthOfProjectedVector / lineVector.length());
		return isPointInRectangle(scaledProjectedVector.getValue(0), scaledProjectedVector.getValue(1),
				fromLineToCircleVector.getValue(0), fromLineToCircleVector.getValue(1), rectWidth, rectHeight);
	}
}
