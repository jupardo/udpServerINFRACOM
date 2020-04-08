package udp.serv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class PortAssigner {
	
	private static final int startPort = 12000;
	
	private static final int endPort = 15000;
	
	public static final PortAssigner portAssign = new PortAssigner();
	
	public static final String file = "appData/ports.data";
	
	private Map<String, Integer> map; 
	
	@SuppressWarnings("unchecked")
	private PortAssigner() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			map = (Map<String, Integer>) ois.readObject();
		} catch(FileNotFoundException fnf) {
			File add = new File(file);
			try {
				add.createNewFile();
				map = new HashMap<String, Integer>();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, Integer> getServicesList() {
		return map;
	}
	
	public Integer assignServiceToPort(String fileName) {
		ArrayList<Integer> availablePorts = new ArrayList<>();
		for(int i = startPort; i < endPort; i++) {
			availablePorts.add(i);
		}
		availablePorts.stream().filter(value -> !map.containsValue(value));
		Integer port = availablePorts.get(0);
		map.put(fileName, port);
		updateData();
		return port;
	}
	
	public boolean removeServiceFromPort(Integer port) {
		Optional<Entry<String, Integer>> entrada = map.entrySet().stream().filter(e -> e.getValue().equals(port)).findAny();
		if(entrada.isPresent()) {
			String service = entrada.get().getKey();
			map.remove(service);
			updateData();
			return true;
		} else {
			return false;
		}
	}
	
	private void updateData() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, false));
			oos.writeObject(map);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
