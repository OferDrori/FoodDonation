package dts.display;

import java.util.List;

public class Reciever {
	
	private String name;
	private List<Food> foodsToTake;
	private PickupTime pickupTime;
	
	public Reciever() {
		
	}
	
	public Reciever(String name, List<Food> foods, PickupTime pickupTime) {
		super();
		this.name = name;
		this.foodsToTake = foods;
		this.pickupTime = pickupTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Food> getFoodsToTake() {
		return foodsToTake;
	}

	public void setFoodsToTake(List<Food> foods) {
		this.foodsToTake = foods;
	}

	public PickupTime getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(PickupTime pickupTime) {
		this.pickupTime = pickupTime;
	}
	
	
	
	
	
	
}
