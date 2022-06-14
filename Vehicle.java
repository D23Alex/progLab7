import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Vehicle implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4529417976611312736L;
	
	private Long id; //���� �� ����� ���� null, �������� ���� ������ ���� ������ 0, �������� ����� ���� ������ ���� ����������, �������� ����� ���� ������ �������������� �������������
    private String name; //���� �� ����� ���� null, ������ �� ����� ���� ������
    private Coordinates coordinates; //���� �� ����� ���� null
    private java.time.LocalDate creationDate; //���� �� ����� ���� null, �������� ����� ���� ������ �������������� �������������
    private long enginePower; //�������� ���� ������ ���� ������ 0
    private VehicleType type; //���� �� ����� ���� null
    private FuelType fuelType; //���� ����� ���� null
    
    private String creator;
    
    Vehicle(VehicleType type, Long id, String name, Coordinates coordinates, java.time.LocalDate creationDate, long enginePower, FuelType fuelType, String creator) {
    	this.id = id;
    	this.name = name;
    	this.coordinates = coordinates;
    	this.creationDate = creationDate;
    	this.enginePower = enginePower;
    	this.fuelType = fuelType;
    	this.type = type;
    	this.creator = creator;
    }
    
    Vehicle() {
    	
    }
    
    
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Coordinates getCoordinates() {
		return coordinates;
	}


	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}


	public java.time.LocalDate getCreationDate() {
		return creationDate;
	}

	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	public void setCreationDate(java.time.LocalDate creationDate) {
		this.creationDate = creationDate;
	}


	public long getEnginePower() {
		return enginePower;
	}


	public void setEnginePower(long enginePower) {
		this.enginePower = enginePower;
	}


	public VehicleType getType() {
		return type;
	}


	public void setType(VehicleType type) {
		this.type = type;
	}


	public FuelType getFuelType() {
		return fuelType;
	}


	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}


	@Override
    public String toString() {
    	return this.type.toString() + " " + this.name + ":\nid: " + this.id + "\ncreation date: " + this.creationDate + "\nfuel type: " + this.getFuelType().toString() + "\ncoordinates: (" + this.coordinates.getX() + ", " + this.coordinates.getY() + ")\nengine power: " + this.enginePower;
    }

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
}
