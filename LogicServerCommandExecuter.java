import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * ПЕРЕИМЕНОВАТЬ
 * В этом классе находится логика исполнения команд от пользователя логическим сервером.
 * Некоторые команды выполняются одним лишь запросом к БД(через клиентский модуль),
 * в то время как другие требуют дополнительной логики,
 * например, сортировка по определённому критерию, выборка и т.п.
 * @author Алексей
 *
 */
public class LogicServerCommandExecuter implements ILogicServerCommandExecuter, IUserToServerCommandExecuter {

	private ILogicToDataServerCommandExecuter databaseClientModule;
	
	private ILogicServerReceiver receiverModule;
	
	private IRequestResponser responserModule;
	
	
	/**
	 * Надо сделать так: если коллекции с таким именем ещё нет, то её необходимо создать
	 * Проверка паролья реализована тут же
	 * @Override
	 */
	public void executeRequest(ILogicServerRequest currentRequest) {
		
		// возможно нам пришёл запрос на создание юзера, в таком случае проверка на пароль не делается.
		
		// проверяем пароль. Если пароль неверный - то сообщаем об этом в ответе и более ничего не делаем
		if (currentRequest.isPasswordCheckRequired()) {
			try {
				if (!this.databaseClientModule.isPasswordCorrect(currentRequest.getUserName(), currentRequest.getPassword())) {
					this.responserModule.makePasswordIncorrect();
					this.responserModule.makeRequestUnsuccessful();
					
				}
			} catch (SQLException e1) {
				this.responserModule.makePasswordIncorrect();
				this.responserModule.makeRequestUnsuccessful();
			}
			try {
				this.databaseClientModule.createCollectionIfNotExists(currentRequest.getCollectionName(), currentRequest.getUserName());
			} catch (SQLException e) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("У нас база сломалась ^_^");
				return;
			}
		
		}
		
		
		
		currentRequest.execute(this, this.responserModule);
	}

	@Override
	public Vehicle getById(long id) {
		try {
			return this.databaseClientModule.getById(id);	
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("A vehicle with id " + id + " could not be found");
			return null;
		}
	}

	@Override
	public Vehicle getMin(Criteria criteria) {
		try {
			ArrayList<Vehicle> vehicles = this.databaseClientModule.getAll(this.receiverModule.getCurrentRequest().getCollectionName());
			if (vehicles.size() == 0) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("Your collection is empty so there is no max element");
			return null;
			}
			return vehicles.stream().min(criteria.getComparator()).get();
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
			return null;
		}
	}

	@Override
	public Vehicle getMax(Criteria criteria) {
		try {
			ArrayList<Vehicle> vehicles = this.databaseClientModule.getAll(this.receiverModule.getCurrentRequest().getCollectionName());
			if (vehicles.size() == 0) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("Your collection is empty so there is no max element");
			return null;
			}
			return vehicles.stream().max(criteria.getComparator()).get();
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
			return null;
		}
	}

	@Override
	public ArrayList<Vehicle> getAll() {
		try {
			return this.databaseClientModule.getAll(this.receiverModule.getCurrentRequest().getCollectionName());
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
			return null;
		}
	}

	@Override
	public void add(Vehicle vehicle) {
		try {
			
			if (!this.databaseClientModule.userCanEditCollection(this.receiverModule.getCurrentRequest().getUserName(),
					this.receiverModule.getCurrentRequest().getCollectionName())) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("You are not allowed to add Vehicles to other users view-only collections");
				return;
			}
			
			this.databaseClientModule.add(vehicle, this.receiverModule.getCurrentRequest().getCollectionName());
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
		}
	}

	@Override
	public void updateById(long id, Vehicle vehicle) {
		try {
			
			if (!this.databaseClientModule.userCanEditCollection(this.receiverModule.getCurrentRequest().getUserName(),
					this.receiverModule.getCurrentRequest().getCollectionName())) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("You are not allowed to update Vehicles of other users view-only collections");
				return;
			}
			
			this.databaseClientModule.updateById(vehicle, id);
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
		}
	}

	@Override
	public void removeById(long id) {
		try {
			
			if (!this.databaseClientModule.userCanEditCollection(this.receiverModule.getCurrentRequest().getUserName(),
					this.receiverModule.getCurrentRequest().getCollectionName())) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("You are not allowed to remove Vehicles from other users view-only collections");
				return;
			}
			
			this.databaseClientModule.removeById(id);
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
		}
	}

	@Override
	public void removeAll() {
		try {
			
			if (!this.databaseClientModule.userCanEditCollection(this.receiverModule.getCurrentRequest().getUserName(),
					this.receiverModule.getCurrentRequest().getCollectionName())) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("You are not allowed to remove Vehicles from other users view-only collections");
				return;
			}
			
			this.databaseClientModule.removeAll(this.receiverModule.getCurrentRequest().getCollectionName());
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
		}
	}

	@Override
	public void removeGreater(Vehicle vehicle, Criteria criteria) {
		// Работаем со стейтом, не получится Stream API
		ArrayList<Vehicle> vehicles;
		try {
			
			if (!this.databaseClientModule.userCanEditCollection(this.receiverModule.getCurrentRequest().getUserName(),
					this.receiverModule.getCurrentRequest().getCollectionName())) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("You are not allowed to remove Vehicles from other users view-only collections");
				return;
			}
			
			vehicles = this.databaseClientModule.getAll(this.receiverModule.getCurrentRequest().getCollectionName());
			for (Vehicle currentVehicle : vehicles) {
				if (criteria.getComparator().compare(currentVehicle, vehicle) > 0) {
					// удалить из БД
					this.databaseClientModule.removeById(currentVehicle.getId());
				}
			}
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
		}
	}

	@Override
	public void removeLower(Vehicle vehicle, Criteria criteria) {
		// Работаем со стейтом, не получится Stream API
		ArrayList<Vehicle> vehicles;
		try {
			
			if (!this.databaseClientModule.userCanEditCollection(this.receiverModule.getCurrentRequest().getUserName(),
					this.receiverModule.getCurrentRequest().getCollectionName())) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("You are not allowed to remove Vehicles from other users view-only collections");
				return;
			}
			
			vehicles = this.databaseClientModule.getAll(this.receiverModule.getCurrentRequest().getCollectionName());
			for (Vehicle currentVehicle : vehicles) {
				if (criteria.getComparator().compare(currentVehicle, vehicle) < 0) {
					// удалить из БД
					this.databaseClientModule.removeById(currentVehicle.getId());
				}
			}
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
		}
	}

	@Override
	public void sort(Criteria criteria) {
		ArrayList<Vehicle> vehicles;
		try {
			
			if (!this.databaseClientModule.userCanEditCollection(this.receiverModule.getCurrentRequest().getUserName(),
					this.receiverModule.getCurrentRequest().getCollectionName())) {
				this.responserModule.makeRequestUnsuccessful();
				this.responserModule.addProblem("You are not allowed to sort Vehicles in other users view-only collections");
				return;
			}
			
			vehicles = this.databaseClientModule.getAll(this.receiverModule.getCurrentRequest().getCollectionName());
			vehicles.sort(criteria.getComparator());
			// TODO: ВОТ ТУТ ВЫЛЕТАЕТ ЭКСЕПШН ПОЧЕМУ-ТО
			databaseClientModule.removeAll(this.receiverModule.getCurrentRequest().getCollectionName());
			databaseClientModule.addAll(vehicles, this.receiverModule.getCurrentRequest().getCollectionName());
		} catch (SQLException e) {
			e.printStackTrace();
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
		}
		
	}

	@Override
	public int getCollectionSize() {
		try {
			return this.databaseClientModule.getCollectionSize(this.receiverModule.getCurrentRequest().getCollectionName());
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
			return -1;
		}
	}

	@Override
	public int getAmountLower(Vehicle vehicle, Criteria criteria) {
		try {
			return this.databaseClientModule.getAll(this.receiverModule.getCurrentRequest().getCollectionName()).stream().filter(veh -> criteria.getComparator().compare(veh, vehicle) < 0).collect(Collectors.toList()).size();
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not access this collection - it doesn't exits");
			return -1;
		}
	}
	
	@Override
	public void register(String username, String password) {
		// TODO Почему тут пусто?
		
	}
	
	@Override
	public void changePassword(String user, String newPassword) {
		try {
			this.databaseClientModule.changePassword(user, newPassword);
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("We can not change the password - user with such name doesn't exits");
		}
	}

	@Override
	public boolean isPasswordCorrect(String user, String password) {
		try {
			return this.databaseClientModule.isPasswordCorrect(user, password);
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("User with such name doesn't exist");
			return false;
		}
	}
	
	@Override
	public void createUser(String name, String password) {
		try {
			this.databaseClientModule.createUser(name, password);
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("User with such name already exists");
		}
	}

	@Override
	public void deleteAccount(String user) {
		try {
			this.databaseClientModule.deleteAccount(user);
		} catch (SQLException e) {
			this.responserModule.makeRequestUnsuccessful();
			this.responserModule.addProblem("User with such name doesn't already exists");
		}
		
	}

	public ILogicToDataServerCommandExecuter getDatabaseClientModule() {
		return databaseClientModule;
	}

	public void setDatabaseClientModule(ILogicToDataServerCommandExecuter databaseClientModule) {
		this.databaseClientModule = databaseClientModule;
	}

	public ILogicServerReceiver getReceiverModule() {
		return receiverModule;
	}

	public void setReceiverModule(ILogicServerReceiver receiverModule) {
		this.receiverModule = receiverModule;
	}

	public IRequestResponser getResponserModule() {
		return responserModule;
	}

	public void setResponserModule(IRequestResponser responserModule) {
		this.responserModule = responserModule;
	}

	
	
}
