package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class FileUtils {
	public static void writeExactToFile (File file, String text) {
		try {
			FileOutputStream fos = new FileOutputStream(file);

			fos.write(text.getBytes());
			fos.close();
		}
		catch (IOException e) {
			throw new FileException(e);
		}
	}
	
	public static String readExactFromFile (File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			
			String text = "";
			int c;
			while ((c = fis.read()) != -1) {
				text += (char) c;
			}
			fis.close();
			
			return text;
		}
		catch (IOException e){
			throw new FileException(e);
		}
	}
	
	public static void copyFileExactToLocation (File file, String location) {
		File copiedFile = new File(location);
		writeExactToFile(copiedFile, readExactFromFile(file));
	}

	public static void writeToFile (File file, String text) {
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(text);
			bw.close();
			fw.close();
		}
		catch (IOException e) {
			throw new FileException(e);
		}
	}
	
	public static String readFromFile (File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String text = "";
			String line;
			while ((line = br.readLine()) != null) {
				text += line + "\n";
			}
			fr.close();
			br.close();
			
			return text;
		}
		catch (IOException e){
			throw new FileException(e);
		}
	}
	
	public static void copyFileToLocation (File file, String location) {
		File copiedFile = new File(location);
		writeToFile(copiedFile, readFromFile(file));
	}
	
	private static class FileException extends RuntimeException {
		public FileException(String s) {
			super(s);
		}
		
		public FileException(Throwable t) {
			super(t);
		}
	}
}
