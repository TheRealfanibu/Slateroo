package utilities;

public class StringUtils {
	public static int indexOfRegex(String str, String regex) {
		for(int i = 0; i < str.length(); i++) {
			for(int j = 1; j <= str.length() - i; j++) {
				if(str.substring(i, i + j).matches(regex))
					return i;
			}
		}
		return -1;
	}
}
