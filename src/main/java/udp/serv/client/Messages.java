package udp.serv.client;

public enum Messages {
	AuthenticationReq("Pls auth"),
	SendFileReq("I'll send you a file bro"),
	ListContainers("Pls show me your containers"),
	RemoveContainer("I'll remove a container");
	
	private String message;
	
	private Messages(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
