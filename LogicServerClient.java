import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Почти все методы имеют таsкую структуру: создай запрос определённого вида к БД , отправь, получи ответ
 * Работа с сервером БД осуществляется именно этим классом.
 * В случае, когда ответ от БД не увенчался успехом,
 * методы этого класса возвращают мусор, в то же время делая запрос не успешным.
 * Тут нет бизнес-логики. Каждый метод - одно обращение к БД, получение одного ответа
 * @author Алексей
 *
 */
public class LogicServerClient implements ILogicServerClient, ILogicToDataServerCommandExecuter {
	
	//TODO: убрать возвращение null из ВСЕХ методов здесь, где это есть. Потому что результаты эти потом исполльзуются.
	// Методы здесь должны выбрасывать ошибки, НЕ ОБРАБАТЫВАТЬ И НЕ ПОРТИТЬ ЗАПРОС
	
	private Connection connectionToDB;
	
	private String salt;
	
	private String pepper;

	
	public LogicServerClient(String pepper, String salt, int databasePort, String databaseHostName, String databaseLogin, String databasePassword, String databaseName) {
		super();
		this.salt = salt;
		this.pepper = pepper;
		try {
			this.connectionToDB = DriverManager.getConnection("jdbc:postgresql://" + databaseHostName + ":" + databasePort +"/" + databaseName, databaseLogin, databasePassword);
		} catch (SQLException e) {
			// TODO PRAY
			e.printStackTrace();
		}
	}

	
	@Override
	public synchronized Vehicle getById(long id) throws SQLException  {	
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("SELECT * FROM vehicles WHERE id=?");
		
		preparedStatement.setLong(1, id);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		
		VehicleType vehicleType;
		String name;
		int x;
		float y;
		int enginePower;
		FuelType fuelType;
		Date creationDate;
		String creator;
		
		resultSet.next();
		vehicleType = VehicleType.getVehicleTypeByName(resultSet.getString("vehicle_type"));
		name = resultSet.getString("name");
		x = resultSet.getInt("x");
		y = resultSet.getFloat("y");
		enginePower = resultSet.getInt("engine_power");
		fuelType = FuelType.getFuelTypeByName(resultSet.getString("fuel_type"));
		creationDate = resultSet.getDate("creation_date");
		
		int creatorId = resultSet.getInt("creator");
		preparedStatement = connectionToDB.prepareStatement("SELECT name FROM users WHERE id=?");
		preparedStatement.setInt(1, creatorId);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		creator = resultSet.getString("name");
			      
		return new Vehicle(vehicleType, id, name, new Coordinates(x, y), LocalDate.of(creationDate.getYear() + 1900, creationDate.getMonth(), creationDate.getMonth()), enginePower, fuelType, creator);
	}

	@Override
	public synchronized void removeById(long id) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("DELETE FROM vehicles WHERE id=?");
		
		preparedStatement.setInt(1, (int) id);
		
		preparedStatement.execute();
	}

	@Override
	public synchronized void updateById(Vehicle vehicle, long id) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("UPDATE vehicles SET name=?, x=?, y=?, engine_power=?, vehicle_type=?, fuel_type=? WHERE id=?");
		
		preparedStatement.setString(1, vehicle.getName());
		preparedStatement.setInt(2, vehicle.getCoordinates().getX());
		preparedStatement.setFloat(3, vehicle.getCoordinates().getY());
		preparedStatement.setInt(4, (int) vehicle.getEnginePower());
		preparedStatement.setString(5, vehicle.getType().getTypeAsText());
		preparedStatement.setString(6, vehicle.getFuelType().getFuelTypeAsText());
		
		preparedStatement.setInt(7, (int) id);
		
		preparedStatement.execute();
	}

	@Override
	public synchronized void add(Vehicle vehicle, String collectionName) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("INSERT INTO vehicles (collection, creator, name, x, y, engine_power, vehicle_type, fuel_type) VALUES((SELECT id FROM collections WHERE name=?), (SELECT id FROM users WHERE name=?), ?, ?, ?, ?, ?, ?)");
		// TODO: откуда в getCreator'е берётся null? поробуем такой фикс... но теперь он ломается
		// Проблема в том, что непонятно, что выдаёт нам getName - имя или же айди. Но лучше пусть имя везде
		preparedStatement.setString(1, collectionName);
		// почему тут креайтор представлен не именем, а айди? где оно так ломается?
		preparedStatement.setString(2, vehicle.getCreator());
		preparedStatement.setString(3, vehicle.getName());
		preparedStatement.setInt(4, vehicle.getCoordinates().getX());
		preparedStatement.setFloat(5, vehicle.getCoordinates().getY());
		preparedStatement.setInt(6, (int) vehicle.getEnginePower());
		preparedStatement.setString(7, vehicle.getType().getTypeAsText());
		preparedStatement.setString(8, vehicle.getFuelType().getFuelTypeAsText());
		
		preparedStatement.executeUpdate();
	}

	@Override
	public synchronized void removeAll(String collectionName) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("DELETE FROM vehicles WHERE collection=(SELECT id FROM collections WHERE name=?)");
		
		preparedStatement.setString(1, collectionName);
		
		preparedStatement.execute();
	}

	@Override
	public synchronized void addAll(ArrayList<Vehicle> vehicles, String collectionName) throws SQLException {
		for (Vehicle currentVehicle : vehicles) {
			this.add(currentVehicle, collectionName);
		}
	}

	@Override
	public synchronized int getCollectionSize(String collection) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("SELECT COUNT(*) AS amount FROM vehicles WHERE collection=(SELECT id FROM collections WHERE name=?)");
		
		preparedStatement.setString(1, collection);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		resultSet.next();
		return resultSet.getInt("amount");
	}

	@Override
	public synchronized ArrayList<Vehicle> getAll(String collectionName) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("SELECT * FROM vehicles WHERE collection=(SELECT id FROM collections WHERE name=?)");

		preparedStatement.setString(1, collectionName);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		ArrayList<Vehicle> vehicles = new ArrayList<>();
		VehicleType vehicleType;
		long id;
		String name;
		int x;
		float y;
		int enginePower;
		FuelType fuelType;
		Date creationDate;
		String creator;
		while (resultSet.next()) {
			vehicleType = VehicleType.getVehicleTypeByName(resultSet.getString("vehicle_type"));
			id = resultSet.getInt("id");
			name = resultSet.getString("name");
			x = resultSet.getInt("x");
			y = resultSet.getFloat("y");
			enginePower = resultSet.getInt("engine_power");
			fuelType = FuelType.getFuelTypeByName(resultSet.getString("fuel_type"));
			creationDate = resultSet.getDate("creation_date");


			int creatorId = resultSet.getInt("creator");
			preparedStatement = connectionToDB.prepareStatement("SELECT name FROM users WHERE id=?");
			preparedStatement.setInt(1, creatorId);
			ResultSet resultSet2 = preparedStatement.executeQuery();
			resultSet2.next();
			creator = resultSet2.getString("name");
			
			vehicles.add(new Vehicle(vehicleType, id, name, new Coordinates(x, y), LocalDate.of(creationDate.getYear() + 1900, creationDate.getMonth(), creationDate.getMonth()), enginePower, fuelType, creator));
		}
		
		return vehicles;
	}
	
	public synchronized boolean userCanEditCollection(String userName, String collectionName) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("SELECT collections.view_only AS view_only, users.name as author FROM collections INNER JOIN users ON collections.author = users.id WHERE collections.name=?");
		
		preparedStatement.setString(1, collectionName);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		resultSet.next();
		return (!resultSet.getBoolean("view_only") || userName.equals(resultSet.getString("author")));
	}
	
	public synchronized boolean isPasswordCorrect(String user, String password) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("SELECT password FROM users WHERE name=?");
		
		preparedStatement.setString(1, user);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		resultSet.next();
		String hashedPassword = resultSet.getString("password");
		
		return hashedPassword.equals(pepper + org.apache.commons.codec.digest.DigestUtils.sha256Hex(password) + salt);

	}
	
	@Override
	public synchronized void createUser(String name, String password) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("SELECT COUNT(*) AS amount FROM users WHERE name=?");
		
		preparedStatement.setString(1, name);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		// не будем париться и кинем sqlexception если пользователь с таким именем уже существует (рефакторинг не люблю(очень))
		resultSet.next();
		if (resultSet.getInt("amount") > 0) {
			throw new SQLException();
		}
		
		preparedStatement = connectionToDB.prepareStatement("INSERT INTO users (name, password) VALUES (?, ?)");

		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
		
		preparedStatement.setString(1, name);
		preparedStatement.setString(2, pepper + sha256hex + salt);
		
		// отсюда вылетает ексепшн
		preparedStatement.executeUpdate();
	}
	
	public synchronized void deleteAccount(String name) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("DELETE FROM users WHERE name=?");
		preparedStatement.setString(1, name);
		preparedStatement.executeUpdate();
	}
	
	@Override
	public synchronized void createCollectionIfNotExists(String collectionName, String author) throws SQLException {
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("SELECT COUNT(*) AS amount FROM collections WHERE name=?");
		preparedStatement.setString(1, collectionName);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		resultSet.next();
		if (resultSet.getInt("amount") < 1) {
			preparedStatement = connectionToDB.prepareStatement("INSERT INTO collections (name, author) VALUES (?, (SELECT id FROM users WHERE name=?))");
			preparedStatement.setString(1, collectionName);
			preparedStatement.setString(2, author);
			preparedStatement.executeUpdate();
		}
	}

	@Override
	public synchronized void changePassword(String user, String newPassword) throws SQLException {
		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(newPassword);
		
		PreparedStatement preparedStatement = connectionToDB.prepareStatement("UPDATE users SET password=? WHERE id=(SELECT id as id FROM users WHERE name=?)");
		preparedStatement.setString(1, pepper + sha256hex + salt);
		preparedStatement.setString(2, user);
		preparedStatement.executeUpdate();
	}


	public Connection getConnectionToDB() {
		return connectionToDB;
	}

	public void setConnectionToDB(Connection connectionToDB) {
		this.connectionToDB = connectionToDB;
	}


	public String getSalt() {
		return salt;
	}


	public void setSalt(String salt) {
		this.salt = salt;
	}


	public String getPepper() {
		return pepper;
	}


	public void setPepper(String pepper) {
		this.pepper = pepper;
	}


}
