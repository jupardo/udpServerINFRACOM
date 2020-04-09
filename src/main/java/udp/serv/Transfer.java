package udp.serv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Optional;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import udp.serv.DockerUtilities.PairPortContainer;

public class Transfer {
	private static final int PORT = 3313;
	
	private static final int bufferSize = 512;
	
	//private static final String LOG_FILE = "/log/streamingUDP.txt";
	
	private static DatagramSocket socket;
	
	@SuppressWarnings("unlikely-arg-type")
	public static void main(String[] args) {
		//Inicializamos las utilidades
		PortAssigner portAssigner = PortAssigner.portAssign;
		DockerUtilities dockerUtilities = DockerUtilities.self;
		DockerOrchestator orchestator = new DockerOrchestator(dockerUtilities);
		try {
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
		        final Client cliente;
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
		        		byte[] EOF = {-1};
		        		if(peticion.getData() == EOF) {
		        			cliente.setSendingFile(false);
		        			new Thread(() -> {
			        			try {
			        				FileInputStream fis = new FileInputStream(cliente.getTemporalFileName());
			        				byte[] buffer2 = new byte[512];
			        				int length;
			        				Hasher sha256 = Hashing.sha256().newHasher();
			        				while((length = fis.read(buffer2)) != -1) {
			        					sha256.putBytes(buffer, 0, length);
			        					DatagramPacket msg = cliente.buildUserMessage(buffer2, length);
			        					socket.send(msg);
			        				}
			        				DatagramPacket msg = cliente.buildUserMessage(EOF, EOF.length);
		        					socket.send(msg);
			        				String hashSum = sha256.hash().toString(); 
			        				if(hashSum.equals(cliente.getTemporalHash())) {
			        					PairPortContainer cont = dockerUtilities.buildUdpTransmitter(cliente.getTemporalFileName());
			        					orchestator.addContainer(cont.port, cont.imageId);
			        					String message = "File is being transmitted on: " + cont.port;
			        					msg = cliente.buildUserMessage(message.getBytes(), message.length());
			        					socket.send(msg);
			        				} else {
			        					System.err.println("Client and server hash does not match");
			        				}
			        				cliente.resetTemporalHash();
			        				cliente.setTemporalFileName("");
			        				//String hex = new BigInteger(digest() 
			        			} catch (Exception e) {
			        				System.err.println("Oops! File colud not be processed");
			        			}
		        			}).start();
		        		} else {
		        			File file = new File("data/" + cliente.getTemporalFileName());
		        			file.createNewFile();
		        			FileOutputStream fos = new FileOutputStream("data/" + cliente.getTemporalFileName(), true);
		        			fos.write(peticion.getData());
		        			fos.close();
		        		}
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
