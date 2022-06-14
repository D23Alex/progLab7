
public class LogicServerRequestRemoveGreater extends LogicServerRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7662315651726213046L;

	private Vehicle vehicleToCompare;
	
	private Criteria criteria;

	
	public LogicServerRequestRemoveGreater(String collectionName, Vehicle vehicleToCompare, Criteria criteria, String userName, String password) {
		super(collectionName, userName, password);
		this.vehicleToCompare = vehicleToCompare;
		this.criteria = criteria;
	}

	
	/**
	 * @Override
	 * Просим прислать нам количество удалённых элементов
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		int amountBefore = commandExecuter.getCollectionSize();
		commandExecuter.removeGreater(vehicleToCompare, criteria);
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
