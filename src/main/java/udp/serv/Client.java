package udp.serv;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Client {
	private boolean authenticated;
	final public InetAddress address;
	final public int port;
	private boolean isSendingFile;
	private String temporalHash;
	private String temporalFileName;
	
	public Client(InetAddress address, int port) {
		this.address = address;
		this.port = port;
		this.authenticated = false;
		this.isSendingFile = false;
		this.temporalHash = "";
	}
	
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	//Returns true if user is authenticated
	public boolean authenticateUser(String username, String password) {
		if(AuthModule.auth(username, password)) {
			this.authenticated = true;
			return true;
		} else {
			return false;
		}		
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public boolean isSendingFile() {
		return isSendingFile;
	}

	public void setSendingFile(boolean isSendingFile) {
		this.isSendingFile = isSendingFile;
	}

	public DatagramPacket buildUserMessage(byte[] data, int length) {
		return new DatagramPacket(data, length,address, port);
	}
	
	public void setTemporalHash(String temporalHash) {
		this.temporalHash = temporalHash;
	}
	
	public String getTemporalHash() {
		return temporalHash;
	}
	
	public void resetTemporalHash() {
		this.temporalHash = "";
	}
	
	public String getTemporalFileName() {
		return temporalFileName;
	}

	public void setTemporalFileName(String temporalFileName) {
		this.temporalFileName = temporalFileName;
	}

	//If hostname and port are equals, user is the same
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return obj instanceof DatagramPacket && 
				((DatagramPacket) obj).getAddress().getHostAddress() == address.getHostAddress() &&
				((DatagramPacket) obj).getPort() == port;
	}


}

