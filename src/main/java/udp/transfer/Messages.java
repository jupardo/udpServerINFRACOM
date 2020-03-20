package udp.transfer;

public enum Messages {
	clientIsReadyForConnection("I'm ready motherfoca");
	
	private String message;
	
	private Messages(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
