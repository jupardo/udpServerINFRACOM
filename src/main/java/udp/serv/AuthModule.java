package udp.serv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AuthModule {

	private static final String authFile="appData/authData.properties";
	
	public static boolean auth(String username, String password) {
		try {
			FileReader fr = new FileReader(authFile);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			br.close();
			return line.split(":")[0].equals(username) && 
					line.split(":")[1].equals(password); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Error! Auth file not found. Users won't be able to authenticate");
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error! Auth file could not be readen. Users won't be able to authenticate");
			return false;
		}
	}
}
