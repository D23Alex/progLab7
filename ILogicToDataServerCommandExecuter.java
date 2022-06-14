import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ��������� ��� �������������� � ���-����, ��� �������� �� ��� ������ ��� ������ � ��.
 * �������� ���� ������� � �����-�� ������ ����������� �� �����! ��� ����������� � commandExecuter, ��� �� ��� � ������
 * @author �������
 *
 */
public interface ILogicToDataServerCommandExecuter {
	
	public Vehicle getById(long id) throws SQLException;
	
	public void removeById(long id) throws SQLException;
	
	public void updateById(Vehicle vehicle, long id) throws SQLException;
	
	public void add(Vehicle vehicle, String collectionName) throws SQLException;
	
	public void removeAll(String collectionName) throws SQLException;
	
	public void addAll(ArrayList<Vehicle> vehicles, String collectionName) throws SQLException;
	
	public int getCollectionSize(String collectionName) throws SQLException;
	
	public ArrayList<Vehicle> getAll(String collectionName) throws SQLException;
	
	public boolean userCanEditCollection(String userName, String collectionName) throws SQLException;
	
	public boolean isPasswordCorrect(String user, String password) throws SQLException;

	public void createUser(String name, String password) throws SQLException;

	public void createCollectionIfNotExists(String collectionName, String author) throws SQLException;
	
	public void changePassword(String user, String newPassword) throws SQLException;
	
	public void deleteAccount(String name) throws SQLException;
	
	public void makeCollectionPublic(String collectionName, String userName) throws SQLException;
	
	public void makeCollectionViewOnly(String collectionName, String userName) throws SQLException;
}
