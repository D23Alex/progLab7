import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.commons.lang3.SerializationUtils;

public class LogicServerResponser extends Responser {
	
	public LogicServerResponser(DatagramSocket datagramSocket) {
		super(datagramSocket);
	}


	private ILogicServerReceiver receiverModule;

	
	@Override
	public void sendResponse() throws IOException {
		byte[] arr = SerializationUtils.serialize(this.getCurrentResponse());
		DatagramPacket packetToSend = new DatagramPacket(arr, arr.length, this.receiverModule.getCurrentClientAddress(), this.receiverModule.getCurrentClientPort());
		this.getDatagramSocket().send(packetToSend);
	}


	public ILogicServerReceiver getReceiverModule() {
		return receiverModule;
	}


	public void setReceiverModule(ILogicServerReceiver receiverModule) {
		this.receiverModule = receiverModule;
	}


	@Override
	public void makePasswordIncorrect() {
		this.getCurrentResponse().makePasswordIncorrect();
	}

}
