
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DBTest {
	
	private static String salt = "456";
	private static String pepper = "123";

	public static void main(String[] args) throws SQLException {
		//Vehicle testVehicle = new Vehicle(VehicleType.HOVERBOARD, -1l, "BoverHoard444", new Coordinates(300, 300), LocalDate.now(), 10l, FuelType.ELECTRICITY);
		
		//Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "admin", "admin");
		// пароль jocker'a без соли и перца
		//addUser("basil", "basilpass");
		//System.out.println(isPasswordCorrect("basil", "basilpass"));
		//System.out.println(getById(2));
		//System.out.println(userCanEditCollection("admin", "default"));
		//addVehicle(testVehicle, "jocker", "default");
		//updateById(5, testVehicle);
		//removeById(6);
		
		System.out.println(getCollectionSize("defvault"));;
	}
	
	/**
	 * Метод добавляет нового пользователя в базу. ХЕШИРОВАНИЕ ПАРОЛЯ ЗДЕСЬ ЖЕ.
	 * РАБОТАЕТ
	 * @param name
	 * @param password
	 * @throws SQLException 
	 */
	private static void addUser(String name, String password) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (name, password) VALUES (?, ?)");

		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
		
		preparedStatement.setString(1, name);
		preparedStatement.setString(2, pepper + sha256hex + salt);
		
		preparedStatement.executeUpdate();
		// 2, 6, 4, 7
		
	}
	
	/**
	 * Метод проверяет, является ли указанный пароль для данного юзера верным.
	 * РАБОТАЕТ
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException 
	 */
	private static boolean isPasswordCorrect(String user, String password) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE name=?");
		
		preparedStatement.setString(1, user);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		resultSet.next();
		String hashedPassword = resultSet.getString("password");
		
		return hashedPassword.equals(pepper + org.apache.commons.codec.digest.DigestUtils.sha256Hex(password) + salt);
		
		
		
	}
	
	/**
	 * РАБОТАЕТ
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static Vehicle getById(long id) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM vehicles WHERE id=?");
		
		preparedStatement.setLong(1, id);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		VehicleType vehicleType;
		String name;
		int x;
		float y;
		int enginePower;
		FuelType fuelType;
		Date creationDate;
		
		resultSet.next();
		vehicleType = VehicleType.getVehicleTypeByName(resultSet.getString("vehicle_type"));
		name = resultSet.getString("name");
		x = resultSet.getInt("x");
		y = resultSet.getFloat("y");
		enginePower = resultSet.getInt("engine_power");
		fuelType = FuelType.getFuelTypeByName(resultSet.getString("fuel_type"));
		creationDate = resultSet.getDate("creation_date");
			      
		return new Vehicle(vehicleType, id, name, new Coordinates(x, y), LocalDate.of(creationDate.getYear() + 1900, creationDate.getMonth(), creationDate.getMonth()), enginePower, fuelType);
	}
	
	/**
	 * Метод проверяет, может ли данный пользователь редактировать данную коллекцию
	 * РАБОТАЕТ
	 * @param userName
	 * @param collectionName
	 * @return true, если пользователь является автором коллекции или коллекция доступна
	 * для редактирования всеми (не view_only)
	 * @throws SQLException 
	 */
	public static boolean userCanEditCollection(String userName, String collectionName) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT collections.view_only AS view_only, users.name as author FROM collections INNER JOIN users ON collections.author = users.id WHERE collections.name=?");
		
		preparedStatement.setString(1, collectionName);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		resultSet.next();
		return (!resultSet.getBoolean("view_only") || userName.equals(resultSet.getString("author")));
	}
	
	/**
	 * Внимание - добавляем машину, ставим её сегодняшнюю дату добавления вне зависимости от реальной - спасибо за винмание
	 * РАБОТАЕТ
	 * @param vehicle
	 * @param userName
	 * @param collectionName
	 * @throws SQLException
	 */
	public static void addVehicle(Vehicle vehicle, String userName, String collectionName) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO vehicles (collection, creator, name, x, y, engine_power, vehicle_type, fuel_type) VALUES((SELECT id FROM collections WHERE name=?), (SELECT id from users WHERE name=?), ?, ?, ?, ?, ?, ?)");
		
		preparedStatement.setString(1, collectionName);
		preparedStatement.setString(2, userName);
		preparedStatement.setString(3, vehicle.getName());
		preparedStatement.setInt(4, vehicle.getCoordinates().getX());
		preparedStatement.setFloat(5, vehicle.getCoordinates().getY());
		preparedStatement.setInt(6, (int) vehicle.getEnginePower());
		preparedStatement.setString(7, vehicle.getType().getTypeAsText());
		preparedStatement.setString(8, vehicle.getFuelType().getFuelTypeAsText());
		
		preparedStatement.executeUpdate();
		
	}
	
	/**
	 * РАБОТАЕТ
	 * @param id
	 * @param vehicle
	 * @throws SQLException
	 */
	public static void updateById(long id, Vehicle vehicle) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("UPDATE vehicles SET name=?, x=?, y=?, engine_power=?, vehicle_type=?, fuel_type=? WHERE id=?");
		
		preparedStatement.setString(1, vehicle.getName());
		preparedStatement.setInt(2, vehicle.getCoordinates().getX());
		preparedStatement.setFloat(3, vehicle.getCoordinates().getY());
		preparedStatement.setInt(4, (int) vehicle.getEnginePower());
		preparedStatement.setString(5, vehicle.getType().getTypeAsText());
		preparedStatement.setString(6, vehicle.getFuelType().getFuelTypeAsText());
		
		preparedStatement.setInt(7, (int) id);
		
		preparedStatement.execute();
	}
	
	/**
	 * РАБОТАЕТ
	 * @param id
	 * @throws SQLException
	 */
	public static void removeById(long id) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM vehicles WHERE id=?");
		
		preparedStatement.setInt(1, (int) id);
		
		preparedStatement.execute();
	}
	
	public static int getCollectionSize(String collection) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/vehicleApp", "postgres", "admin");
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS amount FROM vehicles WHERE collection=(SELECT id FROM collections WHERE name=?)");
		
		preparedStatement.setString(1, collection);
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		resultSet.next();
		return resultSet.getInt("amount");
	}

}
