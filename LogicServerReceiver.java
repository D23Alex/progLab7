import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import org.apache.commons.lang3.SerializationUtils;

public class LogicServerReceiver implements ILogicServerReceiver {
	
	private ILogicServerRequest currentRequest;
	
	private int currentClientPort;
	
	private InetAddress currentClientAddress;
	
	
	public LogicServerReceiver(IServerMain coreModule, ILogicServerRequest currentRequest) {
		super();
		this.currentRequest = currentRequest;
	}
	
	public LogicServerReceiver() {
		
	}

	@Override
	public void receive(DatagramPacket currentDatagramPacket) throws IOException {
		
		this.currentClientAddress = currentDatagramPacket.getAddress();
		this.currentClientPort = currentDatagramPacket.getPort();
		
		this.currentRequest = (ILogicServerRequest) SerializationUtils.deserialize(currentDatagramPacket.getData());
	
	}

	public ILogicServerRequest getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(ILogicServerRequest currentRequest) {
		this.currentRequest = currentRequest;
	}

	public int getCurrentClientPort() {
		return currentClientPort;
	}

	public void setCurrentClientPort(int currentClientPort) {
		this.currentClientPort = currentClientPort;
	}

	public InetAddress getCurrentClientAddress() {
		return currentClientAddress;
	}

	public void setCurrentClientAddress(InetAddress currentClientAddress) {
		this.currentClientAddress = currentClientAddress;
	}

	

}
