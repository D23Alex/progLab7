import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogicServerMain implements IServerMain {
	
	private ILogicToDataServerCommandExecuter databaseClientModule;
	
	private DatagramSocket datagramSocket;
	
	private ExecutorService requestRecieverPool = Executors.newCachedThreadPool();

	private ExecutorService requestExecuterPool = Executors.newCachedThreadPool();
	
	private ExecutorService responseSenderPool = Executors.newCachedThreadPool();
	
	
	private class RequestReciever implements Callable<String> {
		
		private ILogicServerReceiver recieverModule;
		
		private IServerResponser responserModule;
		
		private ILogicServerCommandExecuter commandExecuterModule;
		
		private DatagramPacket datagramPacket;

		
		public RequestReciever(ILogicServerReceiver recieverModule, IServerResponser responserModule,
				ILogicServerCommandExecuter commandExecuterModule, DatagramPacket datagramPacket) {
			this.recieverModule = recieverModule;
			this.responserModule = responserModule;
			this.commandExecuterModule = commandExecuterModule;
			this.datagramPacket = datagramPacket;
		}


		@Override
		public String call() throws Exception {
			System.out.println("Ready for a new request");
			// псевдо-получение: на этом этапе пакет у нас уже имеется, нужно только его десериализовать
			// и запомнить адрес и порт клиента
			this.recieverModule.receive(this.datagramPacket);
			System.out.println("Got a new request - executing");
			
			// теперь передаём принятый и десериализованный запрос другому пулу - пулу выполнения
			requestExecuterPool.submit(new RequestExecutor(recieverModule, responserModule, commandExecuterModule));
			
			return "DONE";
		}
		
	}
	
	private class RequestExecutor implements Callable<String> {
		
		private ILogicServerReceiver recieverModule;
		
		private IServerResponser responserModule;
		
		private ILogicServerCommandExecuter commandExecuterModule;
		

		public RequestExecutor(ILogicServerReceiver recieverModule, IServerResponser responserModule,
				ILogicServerCommandExecuter commandExecuterModule) {
			this.recieverModule = recieverModule;
			this.responserModule = responserModule;
			this.commandExecuterModule = commandExecuterModule;
		}

		
		@Override
		public String call() throws Exception {
			System.out.println("Got a new request - executing");
			this.responserModule.resetResponse();
			this.commandExecuterModule.executeRequest(this.recieverModule.getCurrentRequest());
			System.out.println("Request executed - sending the response");
			
			// теперь передаём запрос другому пулу - пулу отправки ответов
			responseSenderPool.submit(new ResponseSender(responserModule));
			
			return "DONE";
		}
		
	}
	
	private class ResponseSender implements Callable<String> {
		
		private IServerResponser responserModule;

		
		public ResponseSender(IServerResponser responserModule) {
			this.responserModule = responserModule;
		}


		@Override
		public String call() throws Exception {
			this.responserModule.sendResponse();
			System.out.println("Response sent");
			return "DONE";
		}
		
		
	}

	@Override
	public void run() throws IOException {
		while (true) {
			
			// происходит finess...
			// Я создаю 3 модуля - ресивер, респонсер и екзекьютер для каждого треда. Эти модули содержат связь
			// друг с другом и с кор модулем и с модуулем для работы с БД,
			// и самое главное - СТЕЙТ НЫНЕШНЕГО КЛИЕНТА И НЫНЕШНЕГО ЗАПРОСА
			// каждый тред получает эти 3 созданные модуля, а модуль для работы с БД треды ДЕЛЯТ между собой
			// В модуле работы с БД все нужные операции synchronized
			
			
			// эти 2 шага я украл у receiver'а и перенёс сюда
			DatagramPacket currentDatagramPacket = new DatagramPacket(new byte[10000], 10000);
			this.getDatagramSocket().receive(currentDatagramPacket);
			
			// тепрь создаются и настраиваются модули, которыми треды не делятся (у каждого треда свои такие модули)
			// это ресивер, респонсер и екзекьютер. Кор и клиент делятся между всеми
			LogicServerCommandExecuter logicServerCommandExecuter = new LogicServerCommandExecuter();
			LogicServerReceiver logicServerReceiver = new LogicServerReceiver();
			LogicServerResponser logicServerResponser = new LogicServerResponser(this.datagramSocket);
			
			logicServerCommandExecuter.setDatabaseClientModule(this.databaseClientModule);
			logicServerCommandExecuter.setReceiverModule(logicServerReceiver);
			logicServerCommandExecuter.setResponserModule(logicServerResponser);
			
			logicServerResponser.setReceiverModule(logicServerReceiver);
			
			// передаём дело пулу потоков, что принимают запрос.
			// Далее с полученым запросом они распорядятся самостоятельно и передадут сначала в пул выполнения,
			// затем в пул ответов
			this.requestRecieverPool.submit(new RequestReciever(logicServerReceiver, logicServerResponser,
					logicServerCommandExecuter, currentDatagramPacket));
			
			
		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}

	public void setDatagramSocket(DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}

	public ILogicToDataServerCommandExecuter getDatabaseClientModule() {
		return databaseClientModule;
	}

	public void setDatabaseClientModule(ILogicToDataServerCommandExecuter databaseClientModule) {
		this.databaseClientModule = databaseClientModule;
	}

}
