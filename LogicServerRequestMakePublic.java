
public class LogicServerRequestMakePublic extends LogicServerRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5432506171082951808L;

	public LogicServerRequestMakePublic(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
	}

	@Override
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		commandExecuter.makeCollectionPublic();
	}

}
