import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * ������ ����� ����������� � ��������� ��������.
 * @author �������
 *
 */
public interface ILogicServerReceiver {
	
	public int getCurrentClientPort();
	
	public void setCurrentClientPort(int port);
	
	public InetAddress getCurrentClientAddress();
	
	public void setCurrentClientAddress(InetAddress address);
	
	public ILogicServerRequest getCurrentRequest();

	public void receive(DatagramPacket currentDatagramPacket) throws IOException;

}
