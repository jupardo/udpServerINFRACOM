package udp.serv.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class Client {
	public static void main(String[] args) throws NumberFormatException, IOException {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the host you want to connect to");
		String hostname = in.nextLine();
		System.out.println("Enter the port you want to connect to");
		String port = in.nextLine();
		InetAddress addr = InetAddress.getByName(hostname);
		DatagramSocket socket = new DatagramSocket(Integer.parseInt(port), addr);
		String choice = "0";
		byte[] buffer = new byte[512];
		do {
			System.out.println(printmenu());
			choice = in.nextLine();
			switch(choice) {
				case "1":
					System.out.println("Enter username");
					String username = in.nextLine();
					System.out.println("Enter password");
					String password = in.nextLine();
					String msg = Messages.AuthenticationReq.getMessage()+":"+
							username+":"+password;
					socket.send(new DatagramPacket(msg.getBytes(), msg.length()));
					DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
					socket.receive(paquete);
					System.out.println(new String(paquete.getData()));
			break;
				case "2":
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
					int result = chooser.showOpenDialog(null);
					if(result != JFileChooser.APPROVE_OPTION) {
						System.out.println("Please select a file");
						continue;
					}
					File selectedFile = chooser.getSelectedFile();
					String name = selectedFile.getName();
					byte[] buffer2= new byte[8192];
				    int length;
				    FileInputStream fis = new FileInputStream(selectedFile);
				    Hasher sha256 = Hashing.sha256().newHasher();
    				while((length = fis.read(buffer2)) > 0) {
    					sha256.putBytes(buffer, 0, length);
    				}
    				byte[] EOF = {-1};
    				String hashSum = sha256.hash().toString();
    				String msg2 = Messages.SendFileReq+":"+hashSum+":"+name;
    				DatagramPacket hash = new DatagramPacket(msg2.getBytes(), msg2.length());
    				socket.send(hash);
    				paquete = new DatagramPacket(buffer, buffer.length);
    				socket.receive(paquete);
    				fis.close();
					if(new String(paquete.getData()).startsWith("Send the file you wanna stream, end your file with buffer EOF")) {
						fis = new FileInputStream(selectedFile);
						length = 0;
						while((length = fis.read(buffer)) > 0) {
							DatagramPacket data = new DatagramPacket(buffer, buffer.length);
		    				socket.send(data);
		    				buffer = new byte[512];
							socket.send(new DatagramPacket(EOF, EOF.length));
							paquete = new DatagramPacket(buffer, buffer.length);
		    				socket.receive(paquete);
							while(paquete.getData() != EOF) {
								paquete = new DatagramPacket(buffer, buffer.length);
			    				socket.receive(paquete);
							}
							paquete = new DatagramPacket(buffer, buffer.length);
							socket.receive(paquete);
							System.out.println(new String(paquete.getData()));
						}
					} else {
						paquete = new DatagramPacket(buffer, buffer.length);
						socket.receive(paquete);
						System.out.println(new String(paquete.getData()));
					}
					break;
				case "3":
					String msg3 = Messages.ListContainers.getMessage();
					DatagramPacket p3 = new DatagramPacket(msg3.getBytes(), msg3.length());
					socket.send(p3);
					paquete = new DatagramPacket(buffer, buffer.length);
					socket.receive(paquete);
					System.out.println(new String(paquete.getData()));
					break;
				case "4":
					System.out.println("To be implemented");
			}
		}
		while(choice != "0");
		in.close();
	}
	
	public static String printmenu() {
		return ("1. Log in\n"
				+ "2. Upload a file to the server\n"
				+ "3. List all media in the server\n"
				+ "4. Remove a file from the server\n"
				+ "0. Disconnect");
	}
}
