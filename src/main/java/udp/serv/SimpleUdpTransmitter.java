package udp.serv;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class SimpleUdpTransmitter {
	private static DatagramSocket socket;
	
	private static final int bufferSize = 512;
	
	public static void main(String[] args) throws IOException {
		Map<String, String> env = System.getenv();
		String file = env.get("FILE");
		int port = Integer.parseInt(env.get("PORT"));
		socket = new DatagramSocket(port);
		socket.setBroadcast(true);
		byte[] buffer = new byte[bufferSize];
		@SuppressWarnings("resource")
		FileInputStream fis = new FileInputStream(file);
		while(true) {
			int bytes = fis.read(buffer);
			if(bytes == -1) {
				fis.reset();
			} else {
				socket.send(new DatagramPacket(buffer, bytes));
			}
		}
	}
}
