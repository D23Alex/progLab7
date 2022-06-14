import java.io.IOException;

/**
 * ���������� ����� ������������ ������ - ������ ��������.
 * ����� ���� ����������� ����� ���������, ����� ���������
 * @author �������
 *
 */
public abstract class Server implements IServer {
	
	/**
	 * �������� ������ ������, ������� ��������� ����������.
	 */
	private IServerMain coreModule;
	
	/**
	 * ������, ������� ���������� ��������� ������.
	 * ������ ������ ����� ��������� ���-�� � ����������� �����.
	 * ���� ������ ���� ���������� ��� ���� ����
	 */
	private IServerResponser responderModule;
	
	
	public Server(IServerMain coreModule, IServerResponser responderModule) {
		super();
		this.coreModule = coreModule;
		this.responderModule = responderModule;
	}

	@Override
	public void run() throws IOException {
		this.coreModule.run();
		
	}
	
	@Override
	public void stop() {
		this.coreModule.stop();
	}

	public IServerMain getCoreModule() {
		return coreModule;
	}

	public void setCoreModule(IServerMain coreModule) {
		this.coreModule = coreModule;
	}

	public IServerResponser getResponderModule() {
		return responderModule;
	}

	public void setResponderModule(IServerResponser responderModule) {
		this.responderModule = responderModule;
	}
}
