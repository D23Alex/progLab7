
public class LogicServerRequestGetMax extends LogicServerRequest{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8113796422969917137L;
	
	private Criteria criteria;

	
	public LogicServerRequestGetMax(String collectionName, Criteria criteria, String userName, String password) {
		super(collectionName, userName, password);
		this.criteria = criteria;
	}

	
	/**
	 * @Override
	 * ������ �������� ���������� ������� �� ��������� ��������
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		responser.setVehicle(commandExecuter.getMax(this.criteria));
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

}
