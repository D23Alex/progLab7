
public class LogicServerRequestInfo extends LogicServerRequest {

	public LogicServerRequestInfo(String collectionName, String userName, String password) {
		super(collectionName, userName, password);
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = -8762868103423120817L;

	
	/**
	 * ��� �������� ����� ���� ������ ���������� ��������� � ���������
	 * @Override
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		responser.setAmount(commandExecuter.getCollectionSize());
	}

}
