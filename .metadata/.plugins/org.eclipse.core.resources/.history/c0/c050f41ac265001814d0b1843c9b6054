package utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public abstract class Utils {
	public static Compiler compiler = new Compiler();
	
	public static <T> T deepCopy (T toCopy) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(baos);
			
			objOut.writeObject(toCopy);
			objOut.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream objIn = new ObjectInputStream(bais);
			return (T) objIn.readObject();
		} catch(IOException e) {
			throw new RuntimeException("IOException during Deep Copy: " + e);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("ObjectInputStream didn't find class during deep copy: " + e);
		}
			
	}
	
	public static void sleep(int time, TimeUnit unit) {
		sleep(unit.toMillis(time));
	}
	
	public static void sleep (long time) {
		try {
			Thread.sleep(time);	
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Interruption during sleeping: " + e.getMessage());
		}
	}
}
