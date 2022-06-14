
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * ������-������� �� 5 ����
 * @author �������
 * @version 0.1
 *
 */
public abstract class ADialogPhase {
	private String message;
	// ��� ���� �������� �� ��, ����� �� �������� ���������� � ���������� ����� ��� ����������� � �����.
	private boolean giveInputInfo;
	
	// ������ ������ �� �������� - ��� app, session ��� action
	private IStructure parent;
	private Set<String> reservedWords;
	
	ADialogPhase(String message, IStructure parent, String ... reservedWords) {
		this.parent = parent;
		this.message = message;
		this.reservedWords = new HashSet<String>();
		// ��������� ��������� ����� - ��� ����� �������� �������.
		for (String currentWord : reservedWords) {
			this.reservedWords.add(currentWord.toUpperCase());
		}
		this.giveInputInfo = false;
	}
	
	public boolean getGiveInputInfo() {
		return this.giveInputInfo;
	}
	
	public void setGiveInputInfo(boolean tf) {
		this.giveInputInfo = tf;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Gets the answer from a user
	 * @return the user answer
	 */
	@SuppressWarnings("resource")
	public String getAnswer() {
		String answer;
		// TODO: ���� ��������� ������ ��?
		Scanner scanner = new Scanner(System.in);
		while (true) {
			// ���� �� ����� ���� "������ ���������� � �����", �� �� ��������� � ������ ��� ���������� ��� ����������, ����� ������ ���� � �������� ���������� � ������������
			// ��������� �� ��������� ����� ��������. � �� ������� ��� ��������, ����� s���������� ����, ����� ����� � ����������
			if (!this.getGiveInputInfo()) {
				System.out.print(this.parent.getStructureDescription() + " | " + this.getMessage() + ": ");
				this.setGiveInputInfo(true);
			}
			else
				System.out.print(this.parent.getStructureDescription() + " | " + this.getMessageWithInputInfo() + ": ");
			try {
				answer = scanner.nextLine();
				while (answer.length() < 1) {
					answer = scanner.nextLine();
				}
			} catch (NoSuchElementException e) {
				scanner.close();
				return null;
			}
			
			/*
			// ����������, ���� �� ������� ���������� ����� ��� BACK TODO: ��������
			while (true) {
				try {
					answer = scanner.nextLine();
					if (answer.length() < 1) {
						continue askingCycle;
					}
					break;
				} catch (NoSuchElementException e) {
					scanner.close();
					return null;
				}
			}
			*/
			// ����� ��������� ����� - �������. ����� - ������� ����� � ��� ������ �������� �������, ����� ������� �� �����
			if (this.reservedWords.contains(answer.toUpperCase())) {
				return answer.toUpperCase();	
			}
			// ����� �� ��������� ����� - ���������!
			if (isValid(answer)) {
				return answer;
			}
			else
				System.out.println(this.parent.getStructureDescription() + " > Input error | " + this.getFailMessage(answer));
		}
	}
	
	// ��������� �������� ������
	public abstract boolean isValid(String answer);
	
	// ���������� message + ���������� � ����� (��������, ����� �������� ���������)
	public abstract String getMessageWithInputInfo();
	
	// �� ��� ����� ������ �������� ������������ ������, � �� ������ ���������, ��� �� ���. ���� ��������� ��� �������� ���������
	public abstract String getFailMessage(String answer);
}
