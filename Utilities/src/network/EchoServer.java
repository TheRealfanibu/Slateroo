package network;

public class EchoServer extends Server{
	
	public EchoServer(Class<? extends DataFromClientHandler> clientHandler, int maxClients, boolean frameVisible) {
		super(clientHandler, maxClients, frameVisible);
	}
	
	public void run() {
		while(super.isWaitingForClients()) {
			super.listenForClientsToAdd();
		}
		System.err.println("Server listening stopped");
	}
}
