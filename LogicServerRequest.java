
public abstract class LogicServerRequest extends Request implements ILogicServerRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6088898441070053768L;

	public LogicServerRequest(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
	}

}
