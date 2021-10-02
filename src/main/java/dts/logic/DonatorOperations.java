package dts.logic;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dts.Constants;
import dts.Jackson;
import dts.boundaries.OperationBoundary;
import dts.data.ItemEntity;
import dts.display.ReservationDetails;
import dts.display.Reservations;
import dts.errors.BadRequest;
import dts.errors.ReservationNotFound;

@Component("donator")
public class DonatorOperations implements FoodDonationOperations, Constants {
	
	/**
	 * This class is responsible for the functionality donator API operations 
	 */
	private Jackson jackson;
	private FoodOperations foodOperations;
	private FoodOperationHelpers foodOperationHelpers;
	private Reservations reservations;
	private ReservationInputVerifcation reservationInputVerifcation;

	@Autowired
	public DonatorOperations(Jackson jackson, FoodOperations foodOperations, 
			FoodOperationHelpers foodOperationHelpers, ReservationInputVerifcation reservationInputVerifcation) {
		this.jackson = jackson;
		this.foodOperations = foodOperations;
		this.foodOperationHelpers = foodOperationHelpers;
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
	public ItemEntity invokeOperation(OperationBoundary operation, ItemEntity itemEntity) {
		
		this.reservations = this.jackson
				.fromStringToReservationsObject(itemEntity.getItemAttributes());
		
		Reservations pickupWindowOperation = this.jackson.fromMapToReservationsObject(operation.getOperationAttributes());
		
		switch (operation.getType()) {
		
		case CREATE_PICKUP_WINDOW:
			this.createPickupWindows(pickupWindowOperation);
			break;
		case CANCEL_PICKUP_WINDOW:
			this.cancelPickupWindow(pickupWindowOperation);
			break;
		case UPDATE_PICKUP_WINDOW:
			this.updatePickupWindow(pickupWindowOperation);
			break;
		case ADD_NEW_DONATOR_FOOD:
			this.reservations = this.foodOperations.addNewFood(this.reservations, 
					pickupWindowOperation);
			break;
		case REMOVE_DONATOR_FOOD:
			this.reservations = this.foodOperations.removeFood(this.reservations, 
					pickupWindowOperation);
			break;
		default:
			throw new BadRequest("Invalid operation type!! Please provide a valid operation");
		}
		
		this.reservations = this.foodOperationHelpers.clearReservations(this.reservations);
		
		itemEntity.setItemAttributes(this.jackson.fromObjectToStringUsingJackson(this.foodOperationHelpers.
				clearReservations(this.reservations)));
		
		return itemEntity;
	}
	
	/**
	 * Creates a new pickup window for a donator.
	 * 
	 * @param newPickupWindows: new pick up windows of the donator.
	 * 
	 */
	private void createPickupWindows(Reservations newPickupWindows) {
		 /**
	     Json input example:
	     
	     	"operationAttributes": {
       "reservasionsDetails": [ 
           {
               "name": "order1",
               "pickupTime": {
                    "start": "2021-01-02T15:27:10.067+00:00",
                    "end": "2021-01-02T16:27:10.067+00:00"
                },
                "pickupLocation": {
                    "lat": 40.11,
                    "lng": 40.11
                },
                "foodsToGive": [ 
                    {
                        "type": "chicken",
                        "numberOfDishes": 30
                    },
                    {
                        "type": "burger",
                        "numberOfDishes": 30
                    },
                ]
           },
           {
                "name": "order3",
                "pickupTime": {
                    "start": "2021-01-02T17:27:10.067+00:00",
                    "end": "2021-01-02T18:27:10.067+00:00"
                },
                "pickupLocation": {
                    "lat": 30.11,
                    "lng": 30.11
                },
                "foodsToGive": [ 
                    {
                        "type": "orange",
                        "numberOfDishes": 20
                    }
                ]
           }
       ]
   }
	     */
		if (this.reservations.getReservasionsDetails() == null) 
			this.reservations.setReservasionsDetails(new ArrayList<ReservationDetails>()); 
			
		this.reservationInputVerifcation.verifyReservations(newPickupWindows);
		
		List<ReservationDetails> allReservationDetails = this.reservations.getReservasionsDetails();
		List<ReservationDetails> allNewReservationsForPickups = newPickupWindows.getReservasionsDetails();
		
		for (ReservationDetails newPickupWindow: allNewReservationsForPickups) {
				
			if (!allReservationDetails.isEmpty()) {
				String newPickupWindowName = newPickupWindow.getName();
				this.reservationInputVerifcation.isReservasionNameValid(allReservationDetails, 
							newPickupWindowName);	
			}	
			allReservationDetails.add(newPickupWindow);
		}	
		this.reservations.	
			setReservasionsDetails(this.foodOperationHelpers.initRecievers(allReservationDetails)); 
	}
	
	/**
	 * cancels a new pickup window for a donator.
	 * 
	 * @param allReservasionsToCancel: pickup windows to cancel.
	 * 
	 * @exception BadRequest - in case the input data is invalid, or the reservations are null/empty
	 */
	private void cancelPickupWindow(Reservations allReservasionsToCancel) {
		
		/*
		 *  Json input example:
		 *  "operationAttributes":  {
       "reservasionsDetails": [ 
           {
               "name": "order1"
           },
           {
               "name": "order2"
           }
       ]
   }
		 */
		List<ReservationDetails> allAvailablePickupWindows = this.reservations.getReservasionsDetails();
		if (allAvailablePickupWindows == null || allAvailablePickupWindows.isEmpty()) 
			throw new BadRequest("cannot perform 'cancel_pickup_window' if there are no reservations!");
		
		boolean isCancelPickupWindowSuccess = false;
		
		List<ReservationDetails> pickupWindowsToCancel = allReservasionsToCancel.getReservasionsDetails();
		if (pickupWindowsToCancel == null || pickupWindowsToCancel.isEmpty())
			throw new BadRequest("Please provide reservations to delete");
		
		for (ReservationDetails reservationToCancel : pickupWindowsToCancel) {

			String reservationNameToCancel = reservationToCancel.getName();
			this.reservationInputVerifcation.verifyReservationName(reservationNameToCancel);
			
			ReservationDetails pickupWindowToCancel = this.foodOperationHelpers.
					findMatchingReservationDetailsByName(allAvailablePickupWindows, reservationNameToCancel);
			
			if (pickupWindowToCancel != null) {
				allAvailablePickupWindows.remove(pickupWindowToCancel);
				isCancelPickupWindowSuccess = true;
			}	
		}
		if (!isCancelPickupWindowSuccess)
			throw new ReservationNotFound("Could not find a reservation to cancel!");
	}
	
	/**
	 * updates an existing pickup window for a donator.
	 * 
	 * @param updatedPickupWindows: all pickup windows to update.
	 * 
	 * @exception BadRequest - in case the input data is invalid, or the reservations are null/empty
	 */
	private void updatePickupWindow(Reservations updatedPickupWindows) {
		// this method only updates the items that exists in the DB (do not add or
		// delete food!!)
		
		/*
		 *  Json input example:
		 *   "operationAttributes": {
       "reservasionsDetails": [ 
           {
               "name": "order1",
               "pickupTime": {
                    "start": "2021-01-02T15:27:10.067+00:00",
                    "end": "2021-01-02T16:27:10.067+00:00"
                },
                "pickupLocation": {
                    "lat": 40.11,
                    "lng": 40.11
                },
                "foodsToGive": [ 
                    {
                        "type": "chicken",
                        "numberOfDishes": 20
                    },
                    {
                        "type": "burger",
                        "numberOfDishes": 20
                    }
                ]
           },
           {
                "name": "order3",
                "pickupTime": {
                    "start": "2021-01-02T17:27:10.067+00:00",
                    "end": "2021-01-02T18:27:10.067+00:00"
                },
                "pickupLocation": {
                    "lat": 30.11,
                    "lng": 30.11
                },
                "foodsToGive": [ 
                    {
                        "type": "orange",
                        "numberOfDishes": 30
                    }
                ]
           }
       ]
   }
		 */
		
		
		List<ReservationDetails> allAvailablePickupWindows = this.reservations.getReservasionsDetails();
		if (allAvailablePickupWindows == null || allAvailablePickupWindows.isEmpty()) 
			throw new BadRequest("cannot perform 'update_pickup_window' if there are no reservations!");
		
		List<ReservationDetails> allUpdatedReservations = updatedPickupWindows.getReservasionsDetails();
		if (allUpdatedReservations == null)
			throw new BadRequest("Please provide a valid reservation to update!");
				
		for (ReservationDetails existingPickupWindow : allAvailablePickupWindows) {

			String existingReservationName = existingPickupWindow.getName();
			this.reservationInputVerifcation.verifyReservationName(existingReservationName);
			
			ReservationDetails updatedPickupWindow = this.foodOperationHelpers.findMatchingReservationDetailsByName(
					allUpdatedReservations, existingReservationName);

			if (updatedPickupWindow != null) {

				existingPickupWindow = this.foodOperationHelpers.updateTime(existingPickupWindow, updatedPickupWindow);
				existingPickupWindow = this.foodOperationHelpers.updateLocation(existingPickupWindow,updatedPickupWindow);
				existingPickupWindow = this.foodOperationHelpers.UpdateFood(existingPickupWindow, updatedPickupWindow);
				
				existingPickupWindow = this.foodOperationHelpers.removeRecieversThatDoNotMatchPickupWindow(existingPickupWindow);
			}
		}
	}

}
