package udp.transfer;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Client {
	private int bytesRead;
	private int bytesWritten;
	final public InetAddress address;
	final public int port;
	
	public Client(InetAddress address, int port) {
		this.address = address;
		this.port = port;
		bytesRead = 0;
	}
	
	public void increaseBytesRead(int length) {
		bytesRead += length;
	}
	
	public DatagramPacket buildUserMessage(byte[] data, int length) {
		bytesWritten += length;
		return new DatagramPacket(data, length,address, port);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return address.getHostAddress();
	}
}
