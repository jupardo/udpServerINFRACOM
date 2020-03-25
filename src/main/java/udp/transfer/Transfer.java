package udp.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import com.google.common.hash.*;
import com.google.common.io.Files;

public class Transfer {

	private static final int PORT = 3312;
	
	private static final int bufferSize = 512;
	
	private static final String LOG_FILE = "/log/transferUDP.txt";
	
	public static void main(String[] args) {
		try {
			Map<String, String> env = System.getenv();
			final String file = env.get("FILE");
			final int users = Integer.parseInt(env.get("CONCURRENT_CONNECTIONS"));
			int currUsers = 0;
			Client[] clientes = new Client[users];
			DatagramSocket socket = new DatagramSocket(PORT);
			byte[] buffer = new byte[bufferSize];
			while(currUsers < users) {
				// Construimos el DatagramPacket para recibir peticiones
		        DatagramPacket peticion =
		          new DatagramPacket(buffer, buffer.length);

		        // Leemos una petición del DatagramSocket
		        socket.receive(peticion);
		        String cadena = new String(peticion.getData());
		        if(cadena.trim().equals(Messages.clientIsReadyForConnection.getMessage())) {
		        	clientes[currUsers] = new Client(peticion.getAddress(), peticion.getPort());
		        	clientes[currUsers].increaseBytesRead(peticion.getLength());
		        	currUsers++;
		        }

		        if(currUsers == users) {
		        	// Se calcula el hash del archivo
		        	FileInputStream fis = new FileInputStream(file);
		        	@SuppressWarnings("deprecation")
					HashCode hash = Files.asByteSource(new File(file)).hash(Hashing.md5());
		        	for(int i = 0; i < clientes.length && clientes[i] != null; i++) {
		        		Client cliente = clientes[i];
		        		String message = "Hash: " +  new String(hash.toString());
		        		DatagramPacket toSend = cliente.buildUserMessage(message.getBytes(), message.length());
		        		socket.send(toSend);
		        	}
		        	int packageLength;
		        	while((packageLength = fis.read(buffer)) != -1) {
		        		for(int i = 0; i < clientes.length && clientes[i] != null; i++) {
		        			Client cliente = clientes[i];
			        		DatagramPacket toSend = cliente.buildUserMessage(buffer, packageLength);
			        		socket.send(toSend);
			        	}
		        	}
		        	for(int i = 0; i < clientes.length && clientes[i] != null; i++) {
	        			Client cliente = clientes[i];
	        			byte[] EOF = {-1}; 
		        		DatagramPacket toSend = cliente.buildUserMessage(EOF, EOF.length);
		        		socket.send(toSend);
		        	}
		        	FileWriter fw = new FileWriter(LOG_FILE);
		        	PrintWriter pw = new PrintWriter(fw);
		        	pw.println(new Date().toString() + ": Data's been sent\n"
		        			+ "Receivers: " + Arrays.toString(clientes) + "\n"
		        			+ "File sent: " + file);
		        	System.out.println( new Date().toString() + ": Data's been sent\n"
		        			+ "Receivers: " + Arrays.toString(clientes) + "\n"
		        			+ "File sent: " + file);
		        	fw.close();
		        	fis.close();
		        }
	        	//in.close();
			}
			socket.close();
        	return;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
