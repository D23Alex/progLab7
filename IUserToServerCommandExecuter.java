import java.util.ArrayList;

/**
 * ������ �������������
 * @author �������
 *
 */
public interface IUserToServerCommandExecuter {
	
	public Vehicle getById(long id);
	
	public Vehicle getMin(Criteria criteria);
	
	public Vehicle getMax(Criteria criteria);
	
	public ArrayList<Vehicle> getAll();
	
	/**
	 * ����� ��������� � ��������� ��������� ������. ���������� ����������� ������.
	 * ����������� ����������� ������ �������� ��� ����,
	 * ����� ����� ���� ������ ������������� ����������� ����, �������� ���� � ����
	 * @param vehicle - ������, ������� ���� ��������
	 */
	public void add(Vehicle vehicle);
	
	public void updateById(long id, Vehicle vehicle);
	
	public void removeById(long id);
	
	public void removeAll();
	
	/**
	 * ������� �� ��������� ��� ��������, ����������� ������ �� ��������� ��������
	 * @param vehicle - ������, � ������� ���������� ���������
	 * @param criteria - ��������, �� �������� ���������� ���������
	 */
	public void removeGreater(Vehicle vehicle, Criteria criteria);
	
	/**
	 * ������� �� ��������� ��� ��������, ������� ������� �� ��������� ��������
	 * @param vehicle - ������, � ������� ���������� ���������
	 * @param criteria - ��������, �� �������� ���������� ���������
	 */
	public void removeLower(Vehicle vehicle, Criteria criteria);
	
	public void sort(Criteria criteria);
	
	public int getCollectionSize();
	
	/**
	 * ������ ���������� �����������, ������� ������ ��������� �� ��������� ��������
	 * @param vehicle - ������ ����������
	 * @param criteria - ������ �������� ��� ���������
	 * @return �����  - ���������� �����������, ������� ������ ��������� �� ��������� ��������
	 */
	public int getAmountLower(Vehicle vehicle, Criteria criteria);
	
	public void register(String username, String password);
	
	public boolean isPasswordCorrect(String user, String password);
	
	public void createUser(String name, String password);
	
	public void changePassword(String user, String newPassword);
	
	public void deleteAccount(String user);
	
	public void makeCollectionViewOnly();
	
	public void makeCollectionPublic();
	
	
}
