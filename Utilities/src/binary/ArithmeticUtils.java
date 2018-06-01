package binary;

public class ArithmeticUtils {
	public static boolean[] nBitAdd(boolean[] a, boolean[] b) {
		boolean[] s = new boolean[a.length + 1]; // nbit result + carry out
		boolean lastCarryOut = false;
		for(int i = a.length - 1; i >= 0 ; i--) { // f.e. From 1010 the 0. index is 1, but we need to start with the last index
			boolean[] result = fullAdd(lastCarryOut, a[i], b[i]);
			s[i + 1] = result[0];
			if(i == 0) 
				s[0] = result[1];
			else
				lastCarryOut = result[1];
		}
		return s;
	}
	
	public static boolean[] nBitSub(boolean[] a, boolean[] b) {
		boolean[] twoComplB = twoComplement(b);
		twoComplB = ignoreCarryOut(twoComplB);
		return nBitAdd(a, twoComplB);
	}
	
	public static boolean[] twoComplement(boolean[] a) {
		boolean[] inverted = LogicUtils.invert(a);
		return increment(inverted);
	}
	
	public static boolean[] increment(boolean[] a) {
		boolean[] nBit1 = new boolean[a.length];
		nBit1[a.length - 1] = true;
		return nBitAdd(a, nBit1);
	}
	
	public static boolean[] halfAdd(boolean a, boolean b) {
		return new boolean[] {a ^ b, a && b}; // output(S), Carry Out(Co)
	}
	
	public static boolean[] fullAdd(boolean ci, boolean a, boolean b) {
		boolean[] ab = halfAdd(a, b);
		boolean[] ci_ab = halfAdd(ab[0], ci);
		return new boolean[]{ci_ab[0], ab[1] || ci_ab[1]}; //FullAdd with two Half adds and an or
	}
	
	public static boolean[] ignoreCarryOut(boolean[] a) {
		boolean[] withoutCarry = new boolean[a.length - 1];
		for(int i = 0; i < withoutCarry.length; i++) {
			withoutCarry[i] = a[i+1];
		}
		return withoutCarry;
	}
}
