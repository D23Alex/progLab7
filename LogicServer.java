import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

/**
 * ��� ��� �����, ��� ����� �� ������� ����������� �������.
 * �� ��������� �� ��������� ��������� ����� �������.
 * �������� loop ������� � ������ coreModule
 * @author �������
 *
 */
public class LogicServer extends Server implements ILogicServer {
	
	
	/**
	 * ������ ����� ����������/��������
	 */
	private ILogicServerReceiver recieverModule;
	
	/**
	 * ������ ��� ��������� � ������� ���� ������
	 */
	private ILogicServerClient databaseClientModule;
	
	/**
	 * ������ ���������� ������
	 */
	private ILogicServerCommandExecuter commandExecuterModule;
	
	


	public LogicServer(IServerMain coreModule, IServerResponser responderModule, ILogicServerReceiver recieverModule,
			ILogicServerClient databaseClientModule, ILogicServerCommandExecuter commandExecuterModule) {
		super(coreModule, responderModule);
		this.recieverModule = recieverModule;
		this.databaseClientModule = databaseClientModule;
		this.commandExecuterModule = commandExecuterModule;
	}

	public static void main(String args[]) throws IOException {
		int serverPort = 6789;
		String pepper = "123";
		String salt = "321";
		// ��� �����, ���� ������������ � ���� ������, ��������, localhost
		String DatabaseHostName = "localhost";
		int databasePort = 5432;
		String databaseLogin = "postgres";
		String databasePassword = "admin";
		// �������� ���� ������, ��������, vehicleApp
		String databaseName = "vehicleApp";
		
		
		
		Map<String, String> env = System.getenv();
        
        if (env.containsKey("SERVERPORT")) {
        	serverPort = Integer.parseInt(env.get("SERVERPORT"));
        }
        
        if (env.containsKey("SALT")) {
        	salt = env.get("SALT");
        }
        
        if (env.containsKey("PEPPER")) {
        	pepper = env.get("PEPPER");
        }
        
        if (env.containsKey("DBHOSTNAME")) {
        	DatabaseHostName = env.get("DBHOSTNAME");
        }
        
        if (env.containsKey("DATABASE")) {
        	databaseName = env.get("DATABASE");
        }
        
        if (env.containsKey("DBPORT")) {
        	databasePort = Integer.parseInt(env.get("DBPORT"));
        }
        if (env.containsKey("DBLOGIN")) {
        	databaseLogin = env.get("DBLOGIN");
        }
        if (env.containsKey("DBPASS")) {
        	databasePassword = env.get("DBPASS");
        }
        
        System.out.println("Write once - debug everywhere (c) Oracle Foundation 2000");
        System.out.println();
        System.out.println("Ready to launch a server with a database. Picked up options: ");
        System.out.println("ServerPort: " + serverPort);
        System.out.println("DatabaseName: " + databaseName);
        System.out.println("DatabaseHostName: " + DatabaseHostName);
        System.out.println("DatabasePort: " + databasePort);
        System.out.println("DatabseLogin: " + databaseLogin);
        System.out.println("Dataabse password: " + databasePassword);
        System.out.println("Salt: " + salt);
        System.out.println("pepper:" + pepper);
        
		// ��������, �������� ����� ��� ���� ������� 1 � ��� �� - ������� �� ��������
        DatagramSocket datagramSocket = new DatagramSocket(serverPort);
		// ���������� ����������� �������
		LogicServerClient logicServerClient = new LogicServerClient(pepper, salt, databasePort, DatabaseHostName, databaseLogin, databasePassword, databaseName);
		LogicServerCommandExecuter logicServerCommandExecuter = new LogicServerCommandExecuter();
		LogicServerMain logicServerMain = new LogicServerMain();
		LogicServerReceiver logicServerReceiver = new LogicServerReceiver();
		LogicServerResponser logicServerResponser = new LogicServerResponser(datagramSocket);
		
		logicServerCommandExecuter.setDatabaseClientModule(logicServerClient);
		logicServerCommandExecuter.setReceiverModule(logicServerReceiver);
		logicServerCommandExecuter.setResponserModule(logicServerResponser);
		
		logicServerMain.setDatagramSocket(datagramSocket);
		logicServerMain.setDatabaseClientModule(logicServerClient);

		logicServerResponser.setReceiverModule(logicServerReceiver);
		
		
		IServer logicServer = new LogicServer(logicServerMain, logicServerResponser, logicServerReceiver, logicServerClient, logicServerCommandExecuter);
	
		logicServer.run();
	}

	public ILogicServerReceiver getRecieverModule() {
		return recieverModule;
	}

	public void setRecieverModule(ILogicServerReceiver recieverModule) {
		this.recieverModule = recieverModule;
	}

	public ILogicServerClient getDatabaseClientModule() {
		return databaseClientModule;
	}

	public void setDatabaseClientModule(ILogicServerClient databaseClientModule) {
		this.databaseClientModule = databaseClientModule;
	}

	public ILogicServerCommandExecuter getCommandExecuterModule() {
		return commandExecuterModule;
	}

	public void setCommandExecuterModule(ILogicServerCommandExecuter commandExecuterModule) {
		this.commandExecuterModule = commandExecuterModule;
	}
	
}
