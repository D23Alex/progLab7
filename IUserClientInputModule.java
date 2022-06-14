import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public interface IUserClientInputModule extends IStructure {

	/**
	 * ����� �� ��������� ����� ����� ������ ���� ���� ���������
	 * � ���������� ������-������ �� ������ �����
	 * @param fileName ��� �����
	 * @return ������-������
	 * @throws IOException, ���������� � �������� �������� ��������,
	 * ���� �� ������� ��������� ���� ��� �� �����-�� ������������
	 */
	public IScript getScript(String fileName) throws IOException;

	/**
	 * ����� �������� ������� � ��� ����������� ��������� � ������������.
	 * �� ��������, ���� �� ����� ������� ��� ��������� ��� BACK
	 * @return �����-����, 0 ������� - ��� �������, ������ ��-������� ���� ���������
	 * @throws InputDeniedByUserException, ���� ������������ ������� ���������
	 * ������ ����� � ������� ��� �������� "BACK"
	 */
	public ArrayList<String> getCommandAndArgs() throws InputDeniedByUserException;

	/**
	 * ����� �������� ����� �� �����
	 * @param fileName - ��� �����
	 * @return ������ - ���������� �����
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public String getTextFromFile(String fileName) throws FileNotFoundException, IOException;

}
