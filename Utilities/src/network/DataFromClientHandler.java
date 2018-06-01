package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class DataFromClientHandler implements Runnable{
	protected Server server;
	
	protected PrintWriter clientWriter;
	protected BufferedReader clientReader;
	
	protected static int clientCount = 0;
	protected static Map<String, Integer> sentMessages = new HashMap<>();
	
	protected boolean running = true;
	
	protected DataFromClientHandler(Socket client, Server server) {
		initIO(client, server);
		clientCount++;
	}
	
	public void initIO(Socket client, Server server) {
		try {
			clientWriter = new PrintWriter(client.getOutputStream());
			clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.server = server;
	}
	
	public void run() {
		String message;
		
		try {
			while((message = clientReader.readLine()) != null && running) {
				deliverData(message);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	protected abstract void deliverData(String message);
}
