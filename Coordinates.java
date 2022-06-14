import java.io.Serializable;

public class Coordinates implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6709351023663764276L;
	
	private Integer x; //������������ �������� ����: 820, ���� �� ����� ���� null
    private float y; //�������� ���� ������ ���� ������ -538
    
    Coordinates(Integer x, float y) {
    	this.x = x;
    	this.y = y;
    }
    
    Coordinates() {
    	
    }

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
    
    
}
