import java.io.Serializable;

public interface IRequest extends Serializable {
	public String getCollectionName();
	public String getUserName();
	public String getPassword();
	public boolean isPasswordCheckRequired();
}
