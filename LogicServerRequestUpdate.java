
public class LogicServerRequestUpdate extends LogicServerRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3683883742011326251L;

	private long vehicleToUpdateId;
	
	private Vehicle newVehicle;

	
	public LogicServerRequestUpdate(String collectionName, long vehicleToUpdateId, Vehicle newVehicle, String userName, String password) {
		super(collectionName, userName, password);
		this.vehicleToUpdateId = vehicleToUpdateId;
		this.newVehicle = newVehicle;
	}

	
	/**
	 * @Override
	 * � �������� ������ ������ ������� ID 
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser) {
		commandExecuter.updateById(vehicleToUpdateId, newVehicle);
		responser.setId(vehicleToUpdateId);
	}

	public long getVehicleToUpdateId() {
		return vehicleToUpdateId;
	}

	public void setVehicleToUpdateId(long vehicleToUpdateId) {
		this.vehicleToUpdateId = vehicleToUpdateId;
	}

	public Vehicle getNewVehicle() {
		return newVehicle;
	}

	public void setNewVehicle(Vehicle newVehicle) {
		this.newVehicle = newVehicle;
	}

}
