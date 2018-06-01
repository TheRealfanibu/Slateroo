package binary;

public class BinaryUtils {
	public static boolean[] parseBooleanArray(String a) {
		String[] sBits = a.split("");
		boolean[] bBits = new boolean[sBits.length];
		for(int i = 0; i < sBits.length; i++) {
			bBits[i] = parseBoolean(sBits[i]);
		}
		return bBits;
	}
	
	public static String parseBinaryString(boolean[] bools) {
		String sBools = "";
		for(boolean b : bools) {
			sBools += b ? "1" : "0";
		}
		return sBools;
	}
	
	public static boolean parseBoolean(String bit) {
		if(!bit.equals("0") && !bit.equals("1"))
			throw new RuntimeException("Binary String doesn't contain a 0 or 1");
		
		return bit.equals("1") ? true : false;
	}
	
	public static String parseBinaryString(boolean bit) {
		return bit ? "1" : "0";
	}
}
