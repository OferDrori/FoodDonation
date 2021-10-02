package dts.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class FoodOperationHelpers implements Constants {

	private Utils utils;
	private ReservationInputVerifcation reservationInputVerifcation;

	@Autowired
	public FoodOperationHelpers(ReservationInputVerifcation verifyValidInput) {
		this.reservationInputVerifcation = verifyValidInput;
	}

	@PostConstruct
	public void init() {
		this.utils = new Utils();
	}
	
	/**
	 * updates a location of an existing pickup
	 * 
	 * @param existingPickupWindow: existing pickup window.
	 * @param updatedReservasion: an updated pickup window
	 * 
	 * @return ReservationDetails: updated existing pickup window.
	 * 
	 */
	public ReservationDetails updateLocation(ReservationDetails existingPickupWindow,
			ReservationDetails updatedReservasion) {

		PickupLocation updatedLocation = updatedReservasion.getPickupLocation();

		if (updatedLocation != null) {
			existingPickupWindow.getPickupLocation().setLat(updatedLocation.getLat());
			existingPickupWindow.getPickupLocation().setLng(updatedLocation.getLng());
		}

		return existingPickupWindow;
	}
	
	/**
	 * updates a pickup time of an existing pickup
	 * 
	 * @param existingPickupWindow: existing pickup window.
	 * @param updatedReservasion: an updated pickup window
	 * 
	 * @exception BadRequest: in case the date input is invalid.
	 * 
	 * @return ReservationDetails: updated existing pickup window.
	 * 
	 */
	public ReservationDetails updateTime(ReservationDetails existingPickupWindow,
			ReservationDetails updatedReservasion) {

		PickupTime updatedPickupTime = updatedReservasion.getPickupTime();

		if (updatedPickupTime != null) {

			Date startDate = updatedPickupTime.getStart();
			Date endDate = updatedPickupTime.getEnd();

			if (startDate != null && endDate != null && this.utils.compareDates(startDate, endDate) > 0) {
				existingPickupWindow.getPickupTime().setStart(updatedPickupTime.getStart());
				existingPickupWindow.getPickupTime().setEnd(updatedPickupTime.getEnd());
			} else if (startDate != null
					&& this.utils.compareDates(startDate, existingPickupWindow.getPickupTime().getEnd()) > 0) {
				existingPickupWindow.getPickupTime().setStart(updatedPickupTime.getStart());
			} else if (endDate != null
					&& this.utils.compareDates(existingPickupWindow.getPickupTime().getStart(), endDate) > 0) {
				existingPickupWindow.getPickupTime().setEnd(updatedPickupTime.getEnd());
			} else
				throw new BadRequest("The start date occurs before the end date!! invalid!!");
		}

		return existingPickupWindow;
	}

	/**
	 * updates the existing food of an existing pickup
	 * 
	 * @param existingPickupWindow: existing pickup window.
	 * @param updatedReservasion: an updated pickup window
	 * 
	 * @return ReservationDetails: updated existing pickup window.
	 * 
	 */
	public ReservationDetails UpdateFood(ReservationDetails existingPickupWindow,
			ReservationDetails updatedReservasion) {

		List<Food> foodToUpdate = updatedReservasion.getFoodsToGive();
		List<Food> allFoodsAvailable = existingPickupWindow.getFoodsToGive();

		for (Food updatedFood : foodToUpdate) {

			this.reservationInputVerifcation.verifySingleFood(updatedFood, true);

			String updatedFoodType = updatedFood.getType();
			Food existingFoodToUpdate = findMatchingFoodByType(allFoodsAvailable, updatedFoodType);
			int OldFoodIndex = allFoodsAvailable.indexOf(existingFoodToUpdate);

			if (existingFoodToUpdate != null) {

				int updatedFoodNumOfDishes = updatedFood.getNumberOfDishes();
				existingFoodToUpdate.setNumberOfDishes(updatedFoodNumOfDishes);
				allFoodsAvailable.set(OldFoodIndex, existingFoodToUpdate);
			}
		}
		return existingPickupWindow;
	}
	
	/**
	 * finds a matching reservation by a reservation name.
	 * 
	 * @param reservasions: existing pickup windows.
	 * @param reservasionName: reservation name to search for.
	 * 
	 * @return ReservationDetails: a matching reservation details object
	 * 
	 */
	public ReservationDetails findMatchingReservationDetailsByName(List<ReservationDetails> reservasions,
			String reservasionName) {

		for (ReservationDetails reservasionDetails : reservasions) {
			if (!this.utils.isObjectNull(reservasionName)
					&& this.utils.areStringsEqual(reservasionDetails.getName(), reservasionName))
				return reservasionDetails;
		}
		return null;
	}
	
	/**
	 * finds a matching reservation by a reservation name.
	 * 
	 * @param existingFoods: existing foods. 
	 * @param foodType: food type to search for.
	 * 
	 * @return Food: a matching Food object.
	 * 
	 */
	public Food findMatchingFoodByType(List<Food> existingFoods, String foodType) {

		for (Food food : existingFoods) {
			String existingFoodType = food.getType();
			if (this.utils.areStringsEqual(existingFoodType, foodType))
				return food;
		}
		return null;
	}
	
	/**
	 * verifies if food exists according to it's.
	 * 
	 * @param foods: existing foods.
	 * @param foodType: food type to search for.
	 * 
	 * @return boolean: true if food exist, false otherwise.
	 * 
	 */
	public boolean isFoodExist(List<Food> foods, String type) {

		for (Food food : foods) {
			if (this.utils.areStringsEqual(food.getType(), type))
				return true;
		}
		return false;
	}
	
	/**
	 * verifies if date is valid for a new pickup.
	 * 
	 * @param validPickupTime: existing pickup time.
	 * @param recieverPickupTime: reciever pickup time
	 * 
	 * @return boolean: true if the pickup time is valid, false otherwise
	 * 
	 */
	public boolean isDateValidForNewPickup(PickupTime validPickupTime, PickupTime recieverPickupTime) {

		Date validStartDate = validPickupTime.getStart();
		Date validEndDate = validPickupTime.getEnd();

		Date recieverStartDate = recieverPickupTime.getStart();
		Date recieverEndDate = recieverPickupTime.getEnd();

		if (this.utils.compareDates(validStartDate, recieverStartDate) < 0
				|| this.utils.compareDates(validEndDate, recieverEndDate) > 0
				|| this.utils.compareDates(recieverStartDate, recieverEndDate) < 0)
			return false;
		return true;
	}
	
	/**
	 * verifies if foods are valid for a new pickup.
	 * 
	 * @param availableFoods: existing foods.
	 * @param requestedFoods: requested foods.
	 * 
	 * @return boolean: true if the donator has all the foods the receiver wants, false otherwise.
	 * 
	 */
	public boolean isFoodAvailableForNewPickup(List<Food> availableFoods, List<Food> requestedFoods) {

		for (Food requestedFood : requestedFoods) {

			String requestedFoodType = requestedFood.getType();

			Food availableFood = findMatchingFoodByType(availableFoods, requestedFoodType);

			if (availableFood != null) {

				int totalFoodResult = availableFood.getNumberOfDishes() - requestedFood.getNumberOfDishes();
				if (totalFoodResult < 0)
					return false;
			} else
				return false;

		}
		return true;
	}
	
	/**
	 * decreases foods amount from a pickup window
	 * 
	 * @param availableFoods: existing foods.
	 * @param foodsToDecreaseList: requested foods to decrease.
	 * 
	 * @return List<Food>: updated list of food objects that their dishes were decreased.
	 * 
	 */
	public List<Food> decreaseFoodFromReservation(List<Food> availableFoods, List<Food> foodsToDecreaseList) {

		this.reservationInputVerifcation.verifyFoodsParams(foodsToDecreaseList, true);

		for (Food availableFood : availableFoods) {

			String foodsToDecreaseType = availableFood.getType();
			Food food = findMatchingFoodByType(foodsToDecreaseList, foodsToDecreaseType);

			if (food != null) {
				int totalFoodResult = availableFood.getNumberOfDishes() - food.getNumberOfDishes();
				availableFood.setNumberOfDishes(totalFoodResult);
			}
		}
		return availableFoods;
	}

	/**
	 * clears reservations in case date is not valid, or no food is available.
	 * 
	 * @param reservations: reservations object.
	 * 
	 * @return Reservations: updated Reservations object. 
	 * 
	 */
	public Reservations clearReservations(Reservations reservations) {

		Iterator<ReservationDetails> reservationIter = reservations.getReservasionsDetails().iterator();

		while (reservationIter.hasNext()) {

			ReservationDetails reservationDetails = reservationIter.next();

			if (this.utils.compareDates(reservationDetails.getPickupTime().getEnd(), new Date()) >= 0) {
				reservationIter.remove();
				continue;
			}

			Iterator<Food> availableFoodIter = reservationDetails.getFoodsToGive().iterator();
			while (availableFoodIter.hasNext()) {

				Food curAvailableFood = availableFoodIter.next();

				Iterator<Reciever> recieversIter = reservationDetails.getRecievers().iterator();

				while (recieversIter.hasNext()) {

					Reciever curReciever = recieversIter.next();
					Iterator<Food> recieverFoods = curReciever.getFoodsToTake().iterator();

					while (recieverFoods.hasNext()) {

						Food curRecieverFood = recieverFoods.next();
						if (curRecieverFood.getNumberOfDishes() > curAvailableFood.getNumberOfDishes())
							recieverFoods.remove();
					}

					if (curReciever.getFoodsToTake().isEmpty())
						recieversIter.remove();

				}
				if (curAvailableFood.getNumberOfDishes() <= 0)
					availableFoodIter.remove();
			}

			if (reservationDetails.getFoodsToGive().isEmpty())
				reservationIter.remove();
		}

		return reservations;

	}
	
	/**
	 * Verifies if the pickup window fits to the reciever's request.
	 * 
	 * @param existingPickupWindow: existing pickup window
	 * @param newReciever: receiver to check if the pickup window is valid.
	 * 
	 * @exception BadRequest: in case the food or pickup time inputs are invalid.
	 * 
	 * @return boolean: true if the pickup window fits to the receiver request, false otherwise.
	 * 
	 */
	public boolean isWindowValidForReciever(ReservationDetails existingPickupWindow, Reciever newReciever) {

		List<Food> existingFoods = existingPickupWindow.getFoodsToGive();
		List<Food> requestedFood = newReciever.getFoodsToTake();
		if (requestedFood == null || requestedFood.size() == 0)
			throw new BadRequest("Please enter the food required!");

		PickupTime availablePickupTime = existingPickupWindow.getPickupTime();
		PickupTime requestedPickupTime = newReciever.getPickupTime();
		if (requestedPickupTime == null)
			throw new BadRequest("Please enter a pickup time!");

		return isFoodAvailableForNewPickup(existingFoods, requestedFood)
				&& isDateValidForNewPickup(availablePickupTime, requestedPickupTime);

	}
	
	/**
	 * finds a receiver by it's name.
	 * 
	 * @param allAvailableRecievers: existing receivers.
	 * @param recieverName: receiver name to look for.
	 * 
	 * @return Receiver: receiver object in case it was found, null otherwise.
	 * 
	 */
	public Reciever findRecieverByName(List<Reciever> allAvailableRecievers, String recieverName) {

		for (Reciever reciever : allAvailableRecievers) {

			String availableRecieverName = reciever.getName();
			if (this.utils.areStringsEqual(availableRecieverName, recieverName))
				return reciever;
		}
		return null;
	}
	
	/**
	 * removes receivers that do not match an existing pickup window
	 * 
	 * @param existingReservation: existing pickup window.
	 * 
	 * @return ReservationDetails: updated ReservationDetails object.
	 * 
	 */
	public ReservationDetails removeRecieversThatDoNotMatchPickupWindow(ReservationDetails existingReservation) {
		
		List<Reciever> availableRecievers = existingReservation.getRecievers();
		Iterator<Reciever> recieverIter = availableRecievers.iterator();
		
		while(recieverIter.hasNext()) {
			
			Reciever currentReciever = recieverIter.next();
			if (!isWindowValidForReciever(existingReservation, currentReciever)) 
				recieverIter.remove();
		}
		
		return existingReservation;
	}
	
	/**
	 * initialize the receivers array list.
	 * 
	 * @param allReservationsDetails: all the pickup windows objects.
	 * 
	 * @exception BadRequest: in case the donator tries to initialize the receiver array list.
	 * 
	 * @return List<ReservationDetails>: updated list of reservation details.
	 * 
	 */
	public List<ReservationDetails> initRecievers(List<ReservationDetails> allReservationsDetails) {
		
		for (ReservationDetails reservation: allReservationsDetails)
		{
			if (reservation.getRecievers() == null)
				reservation.setRecievers(new ArrayList<Reciever>());
			if (reservation.getRecievers().size() > 0)
				throw new BadRequest("Donators cannot create recievers for the create_pick_window operation!");
		}
		return allReservationsDetails;
	}
}
