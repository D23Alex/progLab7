
public class LogicServerRequestClear extends LogicServerRequest {

	public LogicServerRequestClear(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6903204956564726032L;

	/**
	 * @Override
	 * Запрашиваем в качестве ответа количество удалённых элементов
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		responser.setAmount(commandExecuter.getCollectionSize());
		commandExecuter.removeAll();
	}

}
