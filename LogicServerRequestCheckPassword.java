
public class LogicServerRequestCheckPassword extends LogicServerRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9020760521195909611L;
	
	private String password;

	public LogicServerRequestCheckPassword(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
		this.password = password;
		
		// как бы абсурдно это не выглядело, но:
		this.setPasswordCheckRequired(false);
	}

	@Override
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		if (!commandExecuter.isPasswordCorrect(this.getUserName(), password)) {
			responser.makeRequestUnsuccessful();
		}
	}
	
}
