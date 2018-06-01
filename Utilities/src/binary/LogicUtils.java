package binary;

import java.util.function.BinaryOperator;

public class LogicUtils {
	public static boolean[] invert(boolean[] toInvert) {
		boolean[] inverted = new boolean[toInvert.length];
		for(int i = 0; i < toInvert.length; i++) {
			inverted[i] = toInvert[i] ? false : true;
		}
		return inverted;
	}
	
	public static boolean[] nBitLogical(boolean[] a, boolean[] b, BinaryOperator<Boolean> logic) {
		boolean[] result = new boolean[a.length];
		for(int i = 0; i < a.length; i++) {
			result[i] = logic.apply(a[i], b[i]);
		}
		return result;
	}
}
