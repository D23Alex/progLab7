import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class UserClientFacade implements IUserClientFacade {
	//TODO: сюда можно добавить, например, "Узнать детали подключения, узнать последний запрос и так дальше"
	//TODO: у каждого пользователя есть дефолтная коллекция и он её может менять.
	// по дефолту дать всем пустую коллекцию админа или что-то вроде того
	//TODO: Выводить название коллекции при показе пользователю и автора коллекции
	
	public static void main(String args[]) throws UnknownHostException, IOException {
		String serverName = "localhost";
		int serverPort = 6789;
		
		Map<String, String> env = System.getenv();
		if (env.containsKey("SERVERHOSTNAME")) {
        	serverName = env.get("SERVERHOSTNAME");
        }
		
		if (env.containsKey("SERVERPORT")) {
        	serverPort = Integer.parseInt(env.get("SERVERPORT"));
        }
		
		
		UserClientCore coreModule = new UserClientCore(InetAddress.getByName(serverName), serverPort);
		UserClientInputModule inputModule = new UserClientInputModule();
		UserClientReceiver receiverModule = new UserClientReceiver();
		UserClientRequestSender requestSenderModule = new UserClientRequestSender();
		LogicServerRequestFactoryDefault logicRequestFactory = new LogicServerRequestFactoryDefault();
		
		coreModule.setReceiverModule(receiverModule);
		coreModule.setLogicRequestFactory(logicRequestFactory);
		coreModule.setUserInputModule(inputModule);
		coreModule.setRequestSenderModule(requestSenderModule);
		
		inputModule.setCoreModule(coreModule);
		
		receiverModule.setCoreModule(coreModule);
		
		requestSenderModule.setCoreModule(coreModule);
		
		logicRequestFactory.setCoreModule(coreModule);
		
		UserClientFacade client = new UserClientFacade(coreModule);
		
		client.run();
	}
	
	private IUserClientCore coreModule;
	
	

	public UserClientFacade(IUserClientCore coreModule) {
		this.coreModule = coreModule;
	}

	@Override
	public void run() {
		this.coreModule.run();
	}

	public IUserClientCore getCoreModule() {
		return coreModule;
	}

	public void setCoreModule(IUserClientCore coreModule) {
		this.coreModule = coreModule;
	}

}
