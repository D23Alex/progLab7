import java.util.ArrayList;

/**
 * ��������� ��� ������� ���������������� ��������. ������� ��������� �����
 * @author �������
 *
 */
public interface ILogicRequestFactory {

	/**
	 * ��������� �����. ����� ������ ������ ������������ � ����������� �������
	 * �� ������� ������ �� ������� � ���� � ����������
	 * @param commandAndArguments - ����� �� ������� � ����������
	 * @return ������-������ 
	 * @throws IllegalArgumentException, ���� ��������� ������������
	 */
	public ILogicServerRequest createRequest(ArrayList<String> commandAndArguments) throws IndexOutOfBoundsException, IllegalArgumentException, Exception;


}
