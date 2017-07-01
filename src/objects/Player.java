package objects;

public class Player {
	int id;
	String first_name;
	String last_name;
	String position;
	String real_position;
	String side;
	int weight;	
	String country;


	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	int birth_year;
	int height;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getReal_position() {
		return real_position;
	}
	public void setReal_position(String real_position) {
		this.real_position = real_position;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public int getBirth_year() {
		return birth_year;
	}
	public void setBirth_year(int birth_year) {
		this.birth_year = birth_year;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public Player(int id, String first_name, String last_name, String position, String real_position, String side,
			String country, int birth_year, int height) {
		super();
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.position = position;
		this.real_position = real_position;
		this.side = side;
		this.country = country;
		this.birth_year = birth_year;
		this.height = height;
	}
	public Player() {
		// TODO Auto-generated constructor stub
	}

}
