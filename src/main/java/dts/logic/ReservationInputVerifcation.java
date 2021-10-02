package dts.logic;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import dts.Constants;
import dts.Utils;
import dts.display.Food;
import dts.display.PickupLocation;
import dts.display.PickupTime;
import dts.display.Reciever;
import dts.display.ReservationDetails;
import dts.display.Reservations;
import dts.errors.BadRequest;
import dts.errors.Conflict;

@Component
public class ReservationInputVerifcation implements Constants{
	
	private Utils utils;

	@PostConstruct
	public void init() {
		this.utils = new Utils();
	}
	
	public void verifyFoodsParams(List<Food> foods, boolean isNumOfDishesRequired) {
		
		if (foods == null)
			throw new BadRequest("Please provide a valid foods input!");
		if (foods.isEmpty())
			throw new BadRequest("Foods to donate cannot be empty!");
		
		for (Food food : foods) {
			verifySingleFood(food, isNumOfDishesRequired);
		}
	}

	public void verifySingleFood(Food food, boolean isNumOfDishesRequired) {
		
		String foodType = food.getType();
		if (this.utils.isObjectNull(food))
			throw new BadRequest("Please provide valid food parameters!");
		if (this.utils.isObjectNull(foodType) || this.utils.isStringEmpty(foodType))
			throw new BadRequest("Food type cannot be empty or null!");
		
		Integer numOfDishes = food.getNumberOfDishes();
		
		if (isNumOfDishesRequired)
			if (numOfDishes == null)
				throw new BadRequest("Number of dishes cannot be null");
			else
				if (numOfDishes <= 0)
					throw new BadRequest("food type " + foodType + " cannot have zero or negative number of dishes!!");
		
	}
	
	public void verifyRecieverParams(Reciever reciever, String recieverOperation) {
		
		if (reciever == null)
			throw new BadRequest("Invalid Json input for the reciever!");
		
		String name = reciever.getName();
		verifyReservationName(name);
		
	
		if (this.utils.areStringsEqual(recieverOperation, SCHEDULE_PICKUP)) {
			List<Food> foods = reciever.getFoodsToTake();
			verifyFoodsParams(foods, true);
			PickupTime pickupTime = reciever.getPickupTime();
			verifyPickupTime(pickupTime);
		}
		
	}
	
	public void verifyReservations(Reservations reservationsInput) {
		
		List<ReservationDetails> allReservationsDetails = reservationsInput.getReservasionsDetails();
		if (allReservationsDetails == null || allReservationsDetails.isEmpty())
			throw new BadRequest("Please provide reservations");
		
		for (ReservationDetails reservation: allReservationsDetails) {
			verifySingleReservation(reservation);
		}
	}

	public void verifySingleReservation(ReservationDetails reservation) {
		if (reservation == null)
			throw new BadRequest("Please provide reservation");
		
		String name = reservation.getName();

		PickupTime pickupTime = reservation.getPickupTime();
		PickupLocation pickupLocation = reservation.getPickupLocation();
		List<Food> foods = reservation.getFoodsToGive();
		
		verifyReservationName(name);
		verifyPickupTime(pickupTime);
		verifyPickupLocation(pickupLocation);
		verifyFoodsParams(foods, true);
	}

	public void verifyReservationName(String name) {
		if (name == null)
			throw new BadRequest("Please provide name!");
		
		if (this.utils.areStringsEqual(name, "")) {
			throw new BadRequest("Please provide a valid name!");
		}
	}

	private void verifyPickupLocation(PickupLocation pickupLocation) {
		
		if (pickupLocation == null)
			throw new BadRequest("Please provide a valid pickup location!");
		
		Double lat = pickupLocation.getLat();
		Double lng = pickupLocation.getLng();
		
		if (lat == null || lng == null)
			throw new BadRequest("Please provide a valid lat and lng!");
		
	}

	private void verifyPickupTime(PickupTime pickupTime) {
		
		if (pickupTime == null)
			throw new BadRequest("Please provide valid pickup time!");
		
		Date startDate = pickupTime.getStart();
		Date endDate = pickupTime.getEnd();
		
		if (startDate == null || endDate == null)
			throw new BadRequest("Please provide valid start and end date!");
		
		if (this.utils.compareDates(startDate, endDate) <= 0)
			throw new BadRequest("Start date cannot be after end date!");
		
		if (this.utils.compareDates(startDate, new Date()) > 0)
			throw new BadRequest("Start date must be after the current date!");
	}
	
	public void isReservasionNameValid(List<ReservationDetails> allAvailableReservasions, String ReservasionName) {

		for (ReservationDetails reservasion : allAvailableReservasions) {
			String reservationName = reservasion.getName();
			if (!this.utils.isObjectNull(reservationName) && this.utils.areStringsEqual(reservationName, ReservasionName))
				throw new Conflict("Reservasion with name " + ReservasionName + " already exists!!!");
		}
	}
}
