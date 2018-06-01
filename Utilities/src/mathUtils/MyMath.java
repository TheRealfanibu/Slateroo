package mathUtils;

public class MyMath {
	public static MyCalculator myCalculator = new MyCalculator();
	
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
