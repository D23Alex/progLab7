
public class LogicServerRequestCreateUser extends LogicServerRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8306206564779279612L;
	private String password;
	
	

	public LogicServerRequestCreateUser(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
		this.password = password;
		this.setPasswordCheckRequired(false);
	}

	@Override
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		commandExecuter.createUser(this.getUserName(), password);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
