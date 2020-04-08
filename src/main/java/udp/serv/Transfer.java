package udp.serv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class Transfer {
	private static final int PORT = 3313;
	
	private static final int bufferSize = 512;
	
	private static final String LOG_FILE = "/log/streamingUDP.txt";
	
	private static DatagramSocket socket;
	
	public static void main(String[] args) {
		//Inicializamos las utilidades
		PortAssigner portAssigner = PortAssigner.portAssign;
		DockerUtilities dockerUtilities = DockerUtilities.self;
		DockerOrchestator orchestator = new DockerOrchestator(dockerUtilities);
		try {
			int currUsers = 0;
			ArrayList<Client> clientes = new ArrayList<>();
			socket = new DatagramSocket(PORT);
			byte[] buffer = new byte[bufferSize];
			while(true) {
				// Construimos el DatagramPacket para recibir peticiones
		        DatagramPacket peticion =
		          new DatagramPacket(buffer, buffer.length);

		        // Leemos una petición del DatagramSocket
		        socket.receive(peticion);
		        String cadena = new String(peticion.getData());
		        // Verify if user is stored
		        if(!clientes.contains(peticion)) {
		        	clientes.add(new Client(peticion.getAddress(), peticion.getPort()));
		        }
		        Optional<Client> temp = clientes.stream().filter(c -> c.equals(peticion)).findFirst();
		        Client cliente = null;
		        if(temp.isPresent()) {
		        	cliente = temp.get();
		        } else {
		        	System.err.println("Error: User could not be attached");
		        	continue;
		        }
		        if(!cliente.isAuthenticated()) {
		        	String message;
		        	if(cadena.startsWith(Messages.AuthenticationReq.getMessage())) {
		        		if(cliente.authenticateUser(cadena.split(":")[1], cadena.split(":")[2])) {
		        			message = "You have logged in succesfully";
		        		} else {
		        			message = "Your credentials does not match with any record we have";
		        		}
		        	} else {
		        		message = "Hello! Please authenticate yourself before using the API";
		        	}
		        	DatagramPacket msg = cliente.buildUserMessage(message.getBytes(), message.length());
	        		socket.send(msg);
		        }
		        else {
		        	if(cliente.isSendingFile()) {
		        		
		        	} else {
		        		if(cadena.startsWith(Messages.ListContainers.getMessage())) {
		        			String message = "";
		        			for(String file: portAssigner.getServicesList().keySet()) {
		        				message+= file + " is being streamed on " + portAssigner.getServicesList().get(file) + " port\n";
		        			}
		        			DatagramPacket msg = cliente.buildUserMessage(message.getBytes(), message.length());
			        		socket.send(msg);
		        		}
		        		else if(cadena.startsWith(Messages.SendFileReq.getMessage())) {
		        			cliente.setTemporalHash(cadena.split(":")[1]);
		        			cliente.setSendingFile(true);
		        			String message ="Send the file you wanna stream, end your file with buffer EOF";
		        			DatagramPacket msg = cliente.buildUserMessage(message.getBytes(), message.length());
			        		socket.send(msg);
		        		}
		        	}
		        }
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		if(socket!=null) { 
			socket.close();
		}
		super.finalize();
	}
}
