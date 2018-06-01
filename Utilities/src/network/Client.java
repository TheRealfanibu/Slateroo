package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import utilities.Utils;

public class Client {
	private Socket connection;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private List<String> serverMessages;
	private Thread serverMessageHandler;
	
	private String name;
	private String ip;
	private Class<? extends DataFromServerHandler> classServerHandler;
	
	private int clientIndex = 0;
	
	public Client(Class<? extends DataFromServerHandler> serverHandler, String ip, String name) {
		this.classServerHandler = serverHandler;
		this.name = name;
		this.ip = ip;
	}
	
	public boolean connectToServer(){
		serverMessages = new ArrayList<>();
		try {
			connection = new Socket(ip, Server.PORT);
			OutputStream os = connection.getOutputStream();
			os.write(1);
			os.flush();
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			writer = new PrintWriter(os);
			
			DataFromServerHandler serverHandler = classServerHandler.getConstructor(Client.class).newInstance(this);
			serverMessageHandler = new Thread(serverHandler);
			serverMessageHandler.start();
			
			System.out.println("Erfolgreich verbunden");
			return true;
		} catch(Exception e) {
			System.err.println("Verbinden fehlgeschlagen");
			e.printStackTrace();
			return false;
		}
	}
	

	public synchronized void updateServerMessages(String... messages) {
		for(String message : messages) {
			serverMessages.add(message);
		}
	}
	
	public synchronized List<String> getServerMessages() {
		List<String> oldMessages = Utils.deepCopy(serverMessages);
		serverMessages.clear();
		return oldMessages;
	}
	
	public void writeToServer(String... messages) {
		for(String message : messages) {
			writer.println(message);
			writer.flush();
		}
	}
	
	public void sendNameToServer() {
		writeToServer("Name " + name + " " + clientIndex);
	}
	
	public void setClientIndex(int index) {
		this.clientIndex = index;
		sendNameToServer();
	}
	
	public int getClientIndex() {
		return clientIndex;
	}
	
	public BufferedReader getReader() {
		return reader;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getHostIp() {
		return ip;
	}
}
