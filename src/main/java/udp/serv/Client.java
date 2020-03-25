package udp.serv;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Client {
	private boolean authenticated;
	final public InetAddress address;
	final public int port;
	
	public Client(InetAddress address, int port) {
		this.address = address;
		this.port = port;
		this.authenticated = false;
	}
	
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public DatagramPacket buildUserMessage(byte[] data, int length) {
		return new DatagramPacket(data, length,address, port);
	}
	



}

