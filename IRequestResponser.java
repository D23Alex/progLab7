import java.util.ArrayList;

/**
 * ���� ��������� ������ ��� ����, ����� ����� ������� ����������
 * �� ���� � �������� ��� ������������ ������� ����� �������,
 * ����� ������ ��������������, �� � �������� ������������ �����������. 
 * ��� ������� ������� ��� ���� ����� ��������.
 * � ��� �������� � ������� � �������, � ��� �������� � �������� � �������.
 * ��������� ��������� ������ ��� ����� �����, �� ����������� ����� �� ����� ���������.
 * @author �������
 *
 */
public interface IRequestResponser {
	
	/**
	 * ��������� �������� ���� ������ ������ � ������
	 * @param vehicle - ������, ������� ������ �������� ��� � ������
	 */
	public void add(Vehicle vehicle);
	
	/**
	 * ��������� �������� ��� ��� ������� ����� � ������ � ��������� �������
	 * @param vehicles - ��, ��� ����� ��������
	 */
	public void addAll(ArrayList<Vehicle> vehicles);
	
	public void setAmount(int amount);
	
	public void setId(long id);
	
	public void setVehicle(Vehicle vehicle);
	
	public void addProblem(String problem);
	
	/**
	 * ���������� ���������� �������, �� ������� ��������, ��� "�� ��������"
	 */
	public void makeRequestUnsuccessful();
	
	public void makePasswordIncorrect();
}
