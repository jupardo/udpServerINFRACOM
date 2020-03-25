package udp.serv;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerUtilities {

	final static DockerUtilities self = new DockerUtilities();
	
	private static final String HOST = "tcp://localhost:2375";
	
	public final DockerClient dockerClient; 
	
	private DockerUtilities() {
		//DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(HOST).build();
		dockerClient = DockerClientBuilder.getInstance(HOST).build();
	}
}
