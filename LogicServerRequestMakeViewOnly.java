
public class LogicServerRequestMakeViewOnly extends LogicServerRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 327876597934597084L;

	public LogicServerRequestMakeViewOnly(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
	}

	@Override
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		commandExecuter.makeCollectionViewOnly();
	}

}
