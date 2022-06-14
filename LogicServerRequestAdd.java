
public class LogicServerRequestAdd extends LogicServerRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5579901731683874557L;
	
	/**
	 * Добавляемая машина
	 */
	private Vehicle vehicle;
	
	

	public LogicServerRequestAdd(String collectionName, Vehicle vehicle, String userName, String password) {
		super(collectionName, userName, password);
		this.vehicle = vehicle;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * @Override
	 * Присылать ничего не просим
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		commandExecuter.add(this.vehicle);
	}



}
