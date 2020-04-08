package udp.serv;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;

public class DockerOrchestator {
	private final DockerUtilities dockerUtilities;
	
	private Map<Integer, String> containers;
	
	private final static String DATA = "appData/orchestator.data";
	
	private Thread updateContainerStatus;
	
	public DockerOrchestator(DockerUtilities dockerUtilities) {
		// TODO Auto-generated constructor stub
		this.dockerUtilities = dockerUtilities;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA));
			containers = (Map<Integer, String>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			containers = new HashMap<>();
			persistData();	
		}
		updateContainerStatus = new Thread(new Runnable() {
			@Override
			public void run() {
				// Start containers if not running
				for(String value: containers.values()) {
					try {
						dockerUtilities.dockerClient.startContainerCmd(value).exec();
					} catch (NotModifiedException e) {
						
					} catch (NotFoundException nfe) {
						System.err.println("Warning! Container: " + value + "does not exist");
					}
				}
				//This task should be accomplished each 5 minutes
				try {
					Thread.sleep(300000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		updateContainerStatus.start();
	}
	
	public void addContainer(Integer port, String containerId) {
		containers.put(port, containerId);
		persistData();
	}
	
	private void persistData() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA, false));
			oos.writeObject(containers);
			oos.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void dettachContainer(Integer port) {
		containers.remove(port);
		persistData();
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		updateContainerStatus.interrupt();
		for(String value: containers.values()) {
			try {
				this.dockerUtilities.dockerClient.stopContainerCmd(value).exec();
			} catch(NotFoundException nfe) {
				System.err.println("Warning! Container: " + value + "Does not exist!!. Consider removing it");
			} catch(NotModifiedException nme) {
				
			}
			
		}
		super.finalize();
	}
	
}
