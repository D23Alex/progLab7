
public interface ILogicServerRequest extends IRequest {

	/**
	 * ������  ����� ��������� ����������� ��� ���������������� ���������
	 * � ����������� �������. ������ ������, ��������������,
	 * ����� ����������� �������
	 * @param server ���������� ������, � �������� ���������� request
	 */
	public void execute(IUserToServerCommandExecuter commandExecuter, IRequestResponser responser);
	
	
}
