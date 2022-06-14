import java.time.LocalDate;
import java.util.ArrayList;

public class LogicServerRequestFactoryDefault implements ILogicRequestFactory {
	
	private IUserClientCore coreModule;
	
	//TODO: �������� ��� �����

	@Override
	public ILogicServerRequest createRequest(ArrayList<String> commandAndArguments) 
			throws IllegalArgumentException, IndexOutOfBoundsException {
		String command = commandAndArguments.get(0).toLowerCase();
		try {
			
			//TODO: ����������� ��� �������
			if (command.equals("add")) {
				return new LogicServerRequestAdd(this.coreModule.getCurrentCollectionName(),
						this.createVehicleByCommandAndArguments(commandAndArguments, 1), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("info")) {
				return new LogicServerRequestInfo(this.coreModule.getCurrentCollectionName(), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("show")) {
				return new LogicServerRequestShow(this.coreModule.getCurrentCollectionName(), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("update")) {
				return new LogicServerRequestUpdate(this.coreModule.getCurrentCollectionName(),
						Long.parseLong(commandAndArguments.get(1)),
						this.createVehicleByCommandAndArguments(commandAndArguments, 2), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("remove_by_id")) {
				return new LogicServerRequestRemove(this.coreModule.getCurrentCollectionName(),
						Long.parseLong(commandAndArguments.get(1)), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("clear")) {
				return new LogicServerRequestClear(this.coreModule.getCurrentCollectionName(), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("remove_greater")) {
				return new LogicServerRequestRemoveGreater(this.coreModule.getCurrentCollectionName(),
						this.createVehicleByCommandAndArguments(commandAndArguments,2),
						Criteria.getCriteriaByName(commandAndArguments.get(1)), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("remove_lower")) {
				return new LogicServerRequestRemoveLower(this.coreModule.getCurrentCollectionName(),
						this.createVehicleByCommandAndArguments(commandAndArguments, 2),
						Criteria.getCriteriaByName(commandAndArguments.get(1)), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("sort")) {
				return new LogicServerRequestSort(this.coreModule.getCurrentCollectionName(),
						Criteria.getCriteriaByName(commandAndArguments.get(1)), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("max")) {
				return new LogicServerRequestGetMax(this.coreModule.getCurrentCollectionName(),
						Criteria.getCriteriaByName(commandAndArguments.get(1)), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("group_counting_by_engine_power")) {
				return new LogicServerRequestGroupPower(this.coreModule.getCurrentCollectionName(), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			if (command.equals("count_greater_than_engine_power")) {
				return new LogicServerRequestCountGreaterPower(this.coreModule.getCurrentCollectionName(),
						Long.parseLong(commandAndArguments.get(1)), this.coreModule.getCurrentUserName(), this.coreModule.getCurrentUserPassword());
			}
			
			
			throw new IllegalArgumentException("��� ����� �������");	
				
		} catch (IllegalArgumentException e1) {
			throw e1;
		} catch (IndexOutOfBoundsException e2) {
			// ���� ����������
			throw e2;
		} catch (Exception e3) {
			//TODO: ��������� ���-�� ����������, ��������� IllegalargumentException ��� ����������
			throw new IllegalArgumentException("� ��� �����-�� ������ � ����������");
			
		}
		
	}
	
	/**
	 * ����� �������� ��������� ������� � ����������.
	 * ������ ����� - �������, � ����������� - ��� ��������� ��� ������
	 * @param vehicleTypeArgPosition �������, � ������� ���������� ��������� ��� ������
	 * @return ��������� ������ ������
	 */
	private Vehicle createVehicleByCommandAndArguments(ArrayList<String> commandAndArguments, int vehicleTypeArgPosition) {
		VehicleType vehicleType = VehicleType.getVehicleTypeByName(commandAndArguments.get(vehicleTypeArgPosition + 0));
		String name = commandAndArguments.get(vehicleTypeArgPosition + 1);
		Coordinates coordinates = new Coordinates(Integer.parseInt(commandAndArguments.get(vehicleTypeArgPosition + 2)),
				Float.parseFloat(commandAndArguments.get(vehicleTypeArgPosition + 3)));
		long enginePower = Long.parseLong(commandAndArguments.get(vehicleTypeArgPosition + 4));
		FuelType fuelType = FuelType.getFuelTypeByName(commandAndArguments.get(vehicleTypeArgPosition + 5));
		
		return new Vehicle(vehicleType, -1l, name, coordinates, LocalDate.now(), enginePower, fuelType, this.coreModule.getCurrentUserName());
	}

	public IUserClientCore getCoreModule() {
		return coreModule;
	}

	public void setCoreModule(IUserClientCore coreModule) {
		this.coreModule = coreModule;
	}

	

}
