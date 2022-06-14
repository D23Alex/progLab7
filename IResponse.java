import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * ����� �� ������� �� ������. ������ ������ ��� �� ����������� �������,
 * ��� � �� ������� � ������� ����������� 1 �������, ��� ��� ���������� �� ����������
 * @author �������
 *
 */
public interface IResponse extends Serializable {
	
	/**
	 * ����� ��������, �������� �� ���� ����� ������� �� ��������
	 * ������ ��� ������� �� ���������� ������
	 * @return true, ���� ������, ����� �� ������� ��������� ���� ���������,
	 * ��� �������� �������, ����� falses
	 */
	public boolean isRequestSuccessful();
	
	public void setRequestSuccessful(boolean requestSuccessful);

	public HashSet<String> getProblems();
	
	public void addProblem(String problem);
	
	public long getId();
	
	public int getAmount();
	
	public Vehicle getVehicle();
	
	public ArrayList<Vehicle> getVehicles();
	
	/**
	 * ����� ��������� ��������� ������ � ������������� ArrayList � Respons'�
	 * @param vehicle �� ��� ���� ��������
	 */
	public void addVehicleToList(Vehicle vehicle);
	
	/**
	 * ���������� ������ �������� ��� �������� Vehicle � Respond'�
	 * @param vehicle - ������, ������� ���� ���������
	 */
	public void setVehicle(Vehicle vehicle);
	
	public void setId(long id);
	
	public void setAmount(int amount);
	
	public void makePasswordIncorrect();
	
	public boolean isPasswordCorrect();
	
}
