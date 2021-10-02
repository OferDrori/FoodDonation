package dts.display;

public class Food {
	
	private String type;
	private Integer numberOfDishes;
	
	public Food() {
		
	}
	
	public Food(String type, Integer numberOfDishes) {
		super();
		this.type = type;
		this.numberOfDishes = numberOfDishes;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getNumberOfDishes() {
		return numberOfDishes;
	}

	public void setNumberOfDishes(Integer numberOfDishes) {
		this.numberOfDishes = numberOfDishes;
	}
	

	
	
}
