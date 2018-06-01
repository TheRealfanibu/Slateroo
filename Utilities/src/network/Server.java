package network;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLabel;

public abstract class Server extends JFrame implements Runnable{
	public static final int PORT = 8080;
	
	private ServerSocket server;
	protected List<Socket> clients;
	protected Map<Socket, PrintWriter> clientWriters;
	protected Map<String, Socket> clientsWithName;
	protected Map<Socket, DataFromClientHandler> clientListenerThreads;
	
	private int maxClients;
	
	private JLabel lbServerState;
	
	private boolean clientsLoggedIn = false;
	private boolean waitingForClients = true;
	private boolean painting = false;
	
	private Class<? extends DataFromClientHandler> classClientHandler;
	
	protected Server(Class<? extends DataFromClientHandler> clientHandler, int maxClients, boolean frameVisible) {
		initComponents();
		
		setSize(400, 300);
		addWindowListener(new WindowManager());
		setLayout(new FlowLayout());
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(frameVisible);
		
		this.classClientHandler = clientHandler;
		this.maxClients = maxClients;
	}
	
	public boolean init() {
		try {
			server = new ServerSocket(PORT, maxClients);
			if(!server.isBound()) {
				return false;
			}
			clients = new ArrayList<>();
			clientWriters = new HashMap<>();
			clientsWithName = new HashMap<>();
			clientListenerThreads = new HashMap<>();
			waitingForClients = true;
			swapServerState();
			System.out.println("Server wurde gestartet");
			return true;
		} catch (IOException e) {
			System.err.println("Server wurde nicht gestartet");
			e.printStackTrace();
			return false;
		}
	}
	
	protected boolean listenForClientsToAdd() {
		try {
			try {
				Socket client = server.accept();
				if(client.getInputStream().read() == 0)
					return false;
				
				PrintWriter clientWriter = new PrintWriter(client.getOutputStream());
				if(clientWriters.size() == maxClients) {
					writeToClient(clientWriter, "MaxAmountClients");
					return false;
				}
				clientWriters.put(client, clientWriter);
				clients.add(client);
				
				DataFromClientHandler clientHandler = classClientHandler.getConstructor(Socket.class, Server.class)
						.newInstance(client, this);
				clientListenerThreads.put(client, clientHandler);
				new Thread(clientHandler).start();
				
				writeToClient(clientWriter, "ClientIndex " + clientsWithName.keySet().size());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} catch(NullPointerException e) {
			System.err.println("You need to initialise the Server first!");
		}
		return false;
	}
	
	public void removePlayer(String playerName){
		Socket client = clientsWithName.get(playerName);
		DataFromClientHandler clientListener = clientListenerThreads.get(client);
		clientListener.stop();
		clientWriters.remove(client);
		clientListenerThreads.remove(client);
		clientsWithName.remove(playerName);
		clients.remove(client);
	}
	
	public boolean checkIfNameAlreadyTaken(String name) {
		Set<String> names = clientsWithName.keySet();
		return names.contains(name);
	}
	
	private void writeAllNamesToClient(PrintWriter writer) {
		for (String name : clientsWithName.keySet()) {
			writeToClient(writer, "NewConnection " + name);
		}
	}
	
	public synchronized boolean connectNameWithClient(int indexOfClient, String name) {
		if(!checkIfNameAlreadyTaken(name)) {
			Socket client = clients.get(indexOfClient);
			if(clientsWithName.size() != 0) {
				writeAllNamesToClient(clientWriters.get(client));
			}
			clientsWithName.put(name, client);
			clientsLoggedIn = true;
			this.repaint();
			return true;
		}
		return false;
	}
	
	public void writeToAllClients(String message) {
		for(PrintWriter writer : clientWriters.values()) {
			writer.println(message);
			writer.flush();
		}
	}
	
	public void writeToClient(PrintWriter writer, String message) {
		writer.println(message);
		writer.flush();
	}
	
	public synchronized void paint(Graphics g) {
		if(!painting) {
			painting = true;
			g.drawString("Logged In Clients: ", 20, 65);
			g.drawLine(10, 80, 390, 80);
			
			if(clientsLoggedIn) {
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
				
				int y = 100;
				for(String name : clientsWithName.keySet()) {
					g.clearRect(0, y, getWidth(), 10);
					g.drawString(name, 150, y);
					y += 20;
				}
			}
			painting = false;
		}
		
		
	}
	
	
	private void initComponents() {
		lbServerState = new JLabel("offline");
		
		add(lbServerState);
	}
	
	private void swapServerState() {
		if(lbServerState.getText().equals("offline")) 
			lbServerState.setText("online");
		else
			lbServerState.setText("offline");
	}
	
	protected boolean isWaitingForClients() {
		return waitingForClients;
	}
	
	protected void setWaitingForClients(boolean wait) {
		waitingForClients = wait;
	}
	
	public void stopListeningForClients() {
		waitingForClients = false;
		try {
			Socket s = new Socket("localhost", PORT);
			s.getOutputStream().write(0);
			s.getOutputStream().flush();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		swapServerState();
	}
	
	private class WindowManager extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			writeToAllClients("Alert Server went offline!");
			System.err.println("Server went offline!");
			System.exit(0);
		}
	}
}
