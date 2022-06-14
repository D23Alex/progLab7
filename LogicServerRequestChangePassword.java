
public class LogicServerRequestChangePassword extends LogicServerRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2471211734608365158L;
	
	private String newPassword;
	

	public LogicServerRequestChangePassword(String collectionName, String userName, String password,
			String newPassword) {
		super(collectionName, userName, password);
		this.newPassword = newPassword;
	}

	@Override
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		commandExecuter.changePassword(this.getUserName(), newPassword);
	}


	public String getNewPassword() {
		return newPassword;
	}


	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
