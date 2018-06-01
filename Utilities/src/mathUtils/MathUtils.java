package mathUtils;

public abstract class MathUtils {
	public static double calcSquaredDistance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return dx * dx + dy * dy;
	}
	
	public static double calcDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(calcSquaredDistance(x1, y1, x2, y2));
	}
	
	public static double nSqrt (double n, double z) {
		return Math.pow(z, 1 / n);
	}
	
	public static int fakultaet(int n) {
		if(n == 1)
			return 1;
		else return fakultaet(n-1) * n;
	}
	
	public static String decToBinary(int dec) {
		String bin = "";
		while(dec > 0) {
			bin = dec % 2 + bin;
			dec /= 2;
		}
		return bin;
	}
	
	public static String requireInBounds(String dec) {
		try {
			int d = Integer.parseInt(dec);
			return String.valueOf(d);
		} catch(Exception e) {
			return String.valueOf(Integer.MAX_VALUE);
		}
	}
		
}
