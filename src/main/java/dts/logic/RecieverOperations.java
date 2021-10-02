package dts.logic;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dts.Constants;
import dts.Jackson;
import dts.boundaries.OperationBoundary;
import dts.data.ItemEntity;
import dts.display.Food;
import dts.display.Reciever;
import dts.display.ReservationDetails;
import dts.display.Reservations;
import dts.errors.BadRequest;
import dts.errors.Conflict;
import dts.errors.RecieverNotFound;

@Component("reciever")
public class RecieverOperations implements FoodDonationOperations, Constants {
	
	/**
	 * This class is responsible for the functionality receiver API operations 
	 */
	private Jackson jackson;
	private FoodOperationHelpers foodOperationHelpers;
	private Reservations reservations;
	private ReservationInputVerifcation reservationInputVerifcation;

	@Autowired
	public RecieverOperations(FoodOperationHelpers foodOperationHelpers,
			Jackson jackson, ReservationInputVerifcation reservationInputVerifcation) {
		this.foodOperationHelpers = foodOperationHelpers;
		this.jackson = jackson;
		this.reservationInputVerifcation = reservationInputVerifcation;
	}
	
	/**
	 * Invokes the requested operation from client according to the operation type.
	 * 
	 * @param operation: operationBoundary request
	 * @param itemEntity: an item entity to change.
	 * 
	 * @exception BadRequest: in case the operation is not valid.
	 * 
	 * @return ItemEntity: an item entity that is changed according to the operation performed.
	 */
	@Override
	public ItemEntity invokeOperation(OperationBoundary operation, ItemEntity itemEntity) {

		this.reservations = this.jackson.fromStringToReservationsObject(itemEntity.getItemAttributes());

		Reciever schedulePickupOperation = this.jackson.fromMapToRecieverObject(operation.getOperationAttributes());

		switch (operation.getType()) {

		case SCHEDULE_PICKUP:
			this.schedulePickup(schedulePickupOperation);
			break;
		case CANCEL_SCHEDULE_PICKUP:
			this.cancelSchedulePickup(schedulePickupOperation);
			break;
		case FOOD_DELIVERED:
			this.collectedTheFood(schedulePickupOperation);
			break;
		default:
			throw new BadRequest("Invalid operation type!! Please provide a valid operation");
		}

		this.reservations = this.foodOperationHelpers.clearReservations(this.reservations);

		itemEntity.setItemAttributes(this.jackson.fromObjectToStringUsingJackson(this.reservations));

		return itemEntity;

	}
	
	/**
	 * Receiver uses that to verify the collection of the foods
	 * 
	 * @param removeReciever: reciever that notifies the food is removed.
	 * 
	 * @exception BadRequest: in case the input data is invalid, or the reservations are null/empty
	 * @exception RecieverNotFound: in case the reciever was not found 
	 */
	private void collectedTheFood(Reciever removeReciever) {
		
		/*
		 * Json input example:
		 * 
		 * "operationAttributes": {
        		"name": "reciever2"
   			}
		 */
		List<ReservationDetails> allAvailablePickupWindows = this.reservations.getReservasionsDetails();
		if (allAvailablePickupWindows == null || allAvailablePickupWindows.isEmpty()) 
			throw new BadRequest("cannot perform 'food_delivered' if there are no reservations!");
		this.reservationInputVerifcation.verifyRecieverParams(removeReciever, FOOD_DELIVERED);
		
		String recieverNameToDelete = removeReciever.getName();
		boolean isCollectedFoodSuccess = false;
			
		for (ReservationDetails existingPickupWindow : allAvailablePickupWindows) {

			List<Reciever> allAvailableRecievers = existingPickupWindow.getRecievers();

			Reciever foundReciever = this.foodOperationHelpers.findRecieverByName(allAvailableRecievers,
					recieverNameToDelete);

			if (foundReciever != null) {
				
				List<Food> availableFoods = existingPickupWindow.getFoodsToGive();		
				List<Food> recieverFoods = foundReciever.getFoodsToTake();
	
				existingPickupWindow
						.setFoodsToGive(this.foodOperationHelpers.decreaseFoodFromReservation(availableFoods, recieverFoods));	
				allAvailableRecievers.remove(foundReciever);	
				isCollectedFoodSuccess = true;
				break;
			}
		}
		
		if (!isCollectedFoodSuccess)
			throw new RecieverNotFound("Reciever " + recieverNameToDelete + " was not found!!");
	}
	
	/**
	 * Receiver schedules a pickup
	 * 
	 * @param newReciever: a new receiver to schedule pickup
	 * 
	 * @exception BadRequest: in case the input data is invalid, or there isn't any fit pickup.
	 */
	private void schedulePickup(Reciever newReciever) {
		/*
		 * Json input example:
		 * 
		 * "operationAttributes": {
        		"name": "reciever2",
        		"pickupTime": {
            	"start": "2021-01-02T15:28:10.067+00:00",
            	"end": "2021-01-02T17:26:10.067+00:00"
        		},
        		"foodsToTake": [
            		{
                		"type": "b",
                		"numberOfDishes": 19
            		},
            		{
                		"type": "c",
                		"numberOfDishes": 19
            		}
        		]
   			}
		 */
		List<ReservationDetails> allAvailablePickupWindows = this.reservations.getReservasionsDetails();
		if (allAvailablePickupWindows == null || allAvailablePickupWindows.isEmpty()) 
			throw new BadRequest("cannot perform 'schedule_pickup' if there are no reservations!");
		this.reservationInputVerifcation.verifyRecieverParams(newReciever, SCHEDULE_PICKUP);
		
		boolean isSchedulePickupSuccess = false;
		String newRecieverName = newReciever.getName();
		
		for (ReservationDetails existingPickupWindow : allAvailablePickupWindows) {
			
			List<Reciever> allRecievers = existingPickupWindow.getRecievers();
			if (this.foodOperationHelpers.findRecieverByName(allRecievers, newRecieverName) != null)
				throw new Conflict("Reciever " + newRecieverName + " already exists");
					
			if (this.foodOperationHelpers.isWindowValidForReciever(existingPickupWindow, newReciever)) {

				allRecievers.add(newReciever);
				isSchedulePickupSuccess = true;
				break;
			}
		}

		if (!isSchedulePickupSuccess)
			throw new BadRequest("Unable to find a valid reservation to schedule pickup for " + newRecieverName);
	}
	
	/**
	 * Receiver notifies that he wants to cancel the pickup.
	 * 
	 * @param canceledReciever: the receiver to remove.
	 * 
	 * @exception BadRequest: in case the input data is invalid, or the reservations are null/empty
	 * @exception RecieverNotFound: in case the reciever was not found 
	 */
	private void cancelSchedulePickup(Reciever canceledReciever) {
		
		/*
		 * Json input example:
		 * 
		 * "operationAttributes": {
        		"name": "reciever2"
   			}
		 */
		List<ReservationDetails> allAvailablePickupWindows = this.reservations.getReservasionsDetails();
		if (allAvailablePickupWindows == null || allAvailablePickupWindows.isEmpty()) 
			throw new BadRequest("cannot perform 'cancel_schedule_pickup' if there are no reservations!");
		this.reservationInputVerifcation.verifyRecieverParams(canceledReciever, CANCEL_SCHEDULE_PICKUP);
		
		boolean isCancelSchedulePickupSuccess = false;
	
		String recieverNameToDelete = canceledReciever.getName();
		
		for (ReservationDetails existingPickupWindow : allAvailablePickupWindows) {

			List<Reciever> allAvailableRecievers = existingPickupWindow.getRecievers();

			Reciever foundReciever = this.foodOperationHelpers.findRecieverByName(allAvailableRecievers,
					recieverNameToDelete);

			if (foundReciever != null) {
				allAvailableRecievers.remove(foundReciever);
				isCancelSchedulePickupSuccess = true;
				break;
			}
		}
		
		if (!isCancelSchedulePickupSuccess)
			throw new RecieverNotFound("Reciever " + recieverNameToDelete + " was not found!!");
	}
}
