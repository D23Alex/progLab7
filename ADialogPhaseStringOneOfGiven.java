
/**
 * ������ �� 5 ����
 * @author �������
 * @version 0.1
 *
 */
import java.util.Set;

// �������� �� ���� ��� � ��������� ���������� �������

public class ADialogPhaseStringOneOfGiven extends ADialogPhase {
	
	private Set<String> allowedAnswers;

	ADialogPhaseStringOneOfGiven(Set<String> allowedAnswers, String message, IStructure parent, String ... reservedWords) {
		super(message, parent, reservedWords);
		// �������� ��� �������� � ������� �������
		for (String currentString : allowedAnswers) {
			currentString.toUpperCase();
		}
		this.allowedAnswers = allowedAnswers;
		// ������� "��������� ��������� ��� ����� �����"
		this.setGiveInputInfo(true);
	}

	@Override
	public boolean isValid(String answer) {
		// ������ ��������� ������, ������� ���������� � ��������� ���������� ���������
		return (this.allowedAnswers.contains(answer.toUpperCase()));
	}

	@Override
	public String getMessageWithInputInfo() {
		int counter = 0;
		String enteringMessage = this.getMessage() + " (";
		for (String currentWord : this.allowedAnswers) {
			enteringMessage += currentWord;
			counter ++;
			if (counter < allowedAnswers.size()) {
				enteringMessage += " / ";
			}
		}
		return enteringMessage + ")";
	}

	@Override
	public String getFailMessage(String answer) {
		int counter = 0;
		String failMessage = "You must enter one of the following words: (";
		for (String currentWord : this.allowedAnswers) {
			failMessage += currentWord;
			counter ++;
			if (counter < allowedAnswers.size()) {
				failMessage += " / ";
			}
		}
		failMessage += ") - you entered '" + answer + "'";
		
		return failMessage;
	}

}
