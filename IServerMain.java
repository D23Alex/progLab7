import java.io.IOException;
import java.net.DatagramSocket;

/**
 * ������������� ��� ������!
 * ���� ��������� �������� �� ������ �������� �����
 * ������ ������� - run(), init() � ��� �����
 * @author �������
 *
 */
public interface IServerMain {
	
	public void run() throws IOException;

	public void stop();
	
	public DatagramSocket getDatagramSocket();
	
}
