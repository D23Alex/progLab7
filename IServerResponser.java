import java.io.IOException;
import java.util.ArrayList;

/**
* ������, ������� ���������� ��������� ������.
* ������ ������ ����� ��������� ���-�� � ����������� �����.
* ���� ������ ���� ���������� ��� ���� ����.
* ������ �������� ��� ��� ����������� �������, ��� � ��� ������� ��.
* @author �������
*
*/
public interface IServerResponser {
	
	public void resetResponse();

	/**
	 * ����� �������� ������� ����� �� �������.
	 * ���� ����� ����� �� ������, ���������� ���
	 * @throws IOException 
	 */
	public void sendResponse() throws IOException;
	
	public void add(Vehicle vehicle);
	
	public void addAll(ArrayList<Vehicle> vehicles);
	
	public void addProblem(String problem);
	
	public void setId(long id);
	
	public void setAmount(int amount);
	
	/**
	 * ���������� ���������� �������, �� ������� ��������, ��� "�� ��������"
	 */
	public void makeRequestUnsuccessful();

}
