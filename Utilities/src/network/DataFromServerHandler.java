package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class DataFromServerHandler implements Runnable{
	protected Client client;
	protected BufferedReader clientReader;
	
	protected DataFromServerHandler(Client client) {
		init(client);
	}
	
	public void init(Client client) {
		this.client = client;
		this.clientReader = client.getReader();
	}
	
	public void run() {
		String message;
		
		try {
			while((message = clientReader.readLine()) != null) {
				processData(message);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void processData(String message);
}
