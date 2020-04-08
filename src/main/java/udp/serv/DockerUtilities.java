package udp.serv;

import java.io.File;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerUtilities {

	public final static DockerUtilities self = new DockerUtilities();
	
	private static final String HOST = "tcp://localhost:2375";
	
	public final DockerClient dockerClient; 
	
	private final PortAssigner portUtility = PortAssigner.portAssign;
	
	private DockerUtilities() {
		//DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(HOST).build();
		dockerClient = DockerClientBuilder.getInstance(HOST).build();
	}
	
	public PairPortContainer buildUdpTransmitter(String file) {
		Integer port = portUtility.assignServiceToPort(file);
		BuildImageCmd build = dockerClient.buildImageCmd(
				new File("images/SimpleUDPTransmitter/Dockerfile"))
				.withBuildArg("FILE", file)
				.withBuildArg("PORT", Integer.toString(port));
		BuildImageResultCallback callback = new BuildImageResultCallback() {
		    @Override
		    public void onNext(BuildResponseItem item) {
		       System.out.println("" + item);
		       super.onNext(item);
		    }
		};
		String imageId = build.exec(callback).awaitImageId();
		PairPortContainer container = new PairPortContainer();
		container.port = port;
		container.imageId = imageId;
		return container;
	}
	
	public final class PairPortContainer{
		Integer port;
		String imageId;
	}
}
