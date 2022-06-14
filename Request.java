import java.io.Serializable;

/**
 * ...
 * @author Алексей
 *
 */
public abstract class Request implements IRequest, Serializable {
	
	private String collectionName;
	
	private String userName;
	
	private String password;
	
	private boolean passwordCheckRequired;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7062951802215731357L;
	
	
	public Request(String collectionName, String userName, String password) {
		this.password = password;
		this.collectionName = collectionName;
		this.userName = userName;
		this.passwordCheckRequired = true;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPasswordCheckRequired() {
		return passwordCheckRequired;
	}

	public void setPasswordCheckRequired(boolean passwordCheckRequired) {
		this.passwordCheckRequired = passwordCheckRequired;
	}
	
	
	
}
