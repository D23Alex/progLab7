
public class LogicServerRequestDeleteAccount extends LogicServerRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1020965659642772824L;

	public LogicServerRequestDeleteAccount(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
	}

	@Override
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		commandExecuter.deleteAccount(getUserName());
	}

}
