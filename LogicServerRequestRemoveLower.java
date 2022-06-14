
public class LogicServerRequestRemoveLower extends LogicServerRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7869961024334765608L;

	private Vehicle vehicleToCompare;
	
	private Criteria criteria;

	
	public LogicServerRequestRemoveLower(String collectionName, Vehicle vehicleToCompare, Criteria criteria, String userName, String password) {
		super(collectionName, userName, password);
		this.vehicleToCompare = vehicleToCompare;
		this.criteria = criteria;
	}

	
	/**
	 * @Override
	 * ������ �������� ��� ���������� �������� ���������
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		int amountBefore = commandExecuter.getCollectionSize();
		commandExecuter.removeLower(vehicleToCompare, criteria);
		responser.setAmount(amountBefore - commandExecuter.getCollectionSize());
	}

	public Vehicle getVehicleToCompare() {
		return vehicleToCompare;
	}

	public void setVehicleToCompare(Vehicle vehicleToCompare) {
		this.vehicleToCompare = vehicleToCompare;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}


}
