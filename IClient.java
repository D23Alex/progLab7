import java.io.IOException;
import java.net.InetSocketAddress;

public interface IClient extends IStructure {
	
	/**
	 * ����� ���������, ��������� �� ������ � ������-���� �������.
	 * @return true, ���� ���������, ����� false
	 */
	public boolean isConnected();
	
	/*
	 * ����� ��������� ����������� � ���������� �������
	 */
	public void connect(InetSocketAddress address, int port);

	/**
	 * ����� ����� ������ ����������� ������� � �������
	 * @return ���� ����� ���������� ������ � ��������, �� ����� ����������
	 * TODO: ����������
	 */
	public String getConnectionDetails();
	
	public void sendRequest() throws IOException;
	
	public void receiveResponse() throws IOException;
}
