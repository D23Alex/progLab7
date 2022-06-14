import java.net.DatagramSocket;
import java.util.ArrayList;

public abstract class Responser implements IServerResponser, IRequestResponser {
	
	private IResponse currentResponse;
	private DatagramSocket datagramSocket;
	
	@Override
	public void makeRequestUnsuccessful() {
		this.currentResponse.setRequestSuccessful(false);
	}
	


	public Responser(DatagramSocket datagramSocket) {
		super();
		this.datagramSocket = datagramSocket;
	}



	@Override
	public void resetResponse() {
		this.currentResponse = new Response();
		
	}
	
	@Override
	public void setVehicle(Vehicle vehicle) {
		this.currentResponse.setVehicle(vehicle);
	}

	@Override
	public void add(Vehicle vehicle) {
		this.currentResponse.addVehicleToList(vehicle);
		
	}

	@Override
	public void addAll(ArrayList<Vehicle> vehicles) {
		if (vehicles == null) {
			return;
		}
		for (Vehicle vehicle : vehicles) {
			this.currentResponse.addVehicleToList(vehicle);
		}
		
	}

	@Override
	public void addProblem(String problem) {
		this.currentResponse.addProblem(problem);
	}

	@Override
	public void setId(long id) {
		this.currentResponse.setId(id);
	}

	@Override
	public void setAmount(int amount) {
		this.currentResponse.setAmount(amount);
	}

	public IResponse getCurrentResponse() {
		return currentResponse;
	}

	public void setCurrentResponse(IResponse currentResponse) {
		this.currentResponse = currentResponse;
	}




	public DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}




	public void setDatagramSocket(DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}

}
