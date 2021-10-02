package dts.display;

import java.util.List;

public class ReservationDetails {

	private String name;
	private PickupTime pickupTime;
	private PickupLocation pickupLocation;
	private List<Reciever> recievers;
	private List<Food> foodsToGive;

	public ReservationDetails() {
	}

	public ReservationDetails(String name, PickupTime time, PickupLocation pickupLocation,
			List<Food> foods, List<Reciever> recievers) {
		super();
		this.name = name;
		this.pickupTime = time;
		this.pickupLocation = pickupLocation;
		this.foodsToGive = foods;
		this.recievers = recievers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PickupTime getPickupTime() {
		return pickupTime;
	}

	public void setTime(PickupTime pickupTime) {
		this.pickupTime = pickupTime;
	}

	public PickupLocation getPickupLocation() {
		return pickupLocation;
	}

	public void setPickupLocation(PickupLocation pickupLocation) {
		this.pickupLocation = pickupLocation;
	}

	public List<Food> getFoodsToGive() {
		return foodsToGive;
	}

	public void setFoodsToGive(List<Food> foods) {
		this.foodsToGive = foods;
	}


	public void setPickupTime(PickupTime pickupTime) {
		this.pickupTime = pickupTime;
	}

	public void setFoods(List<Food> foods) {
		this.foodsToGive = foods;
	}

	public List<Reciever> getRecievers() {
		return recievers;
	}

	public void setRecievers(List<Reciever> recievers) {
		this.recievers = recievers;
	}
	
	

}
