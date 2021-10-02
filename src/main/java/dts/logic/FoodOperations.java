package dts.logic;


import java.util.List;

import org.springframework.stereotype.Component;

import dts.display.Food;
import dts.display.Reciever;
import dts.display.ReservationDetails;
import dts.display.Reservations;
import dts.errors.BadRequest;

@Component
public class FoodOperations {

	private FoodOperationHelpers foodOperationHelpers;
	private ReservationInputVerifcation reservationInputVerifcation;

	public FoodOperations(FoodOperationHelpers foodOperationHelpers, ReservationInputVerifcation reservationInputVerifcation) {
		this.foodOperationHelpers = foodOperationHelpers;
		this.reservationInputVerifcation = reservationInputVerifcation;
	}

	public Reservations addNewFood(Reservations allReservations, Reservations addNewFoodReservasions) {

		/*/
		 * 
		 * Json Example:
		 * 
		 * "operationAttributes":  {
       "reservasionsDetails": [ 
           {
               "name": "order2",
                "foodsToGive": [ 
                    {
                        "type": "chicken",
                        "numberOfDishes": 1
                    },
                    {
                        "type": "burger",
                        "numberOfDishes": 1
                    },
                    {
                        "type": "orange",
                        "numberOfDishes": 1
                    },
                    {
                         "type": "rice",
                        "numberOfDishes": 1
                    }
                ]
           },
           {
                "name": "order3",
                "foodsToGive": [ 
                    {
                        "type": "banana",
                        "numberOfDishes": 1
                    }
                ]
           }
       ]
   }
		 */
		
		List<ReservationDetails> allAvailablePickupWindows = allReservations.getReservasionsDetails();
		if (allAvailablePickupWindows == null || allAvailablePickupWindows.isEmpty()) 
			throw new BadRequest("cannot perform 'add_new_donator_food' if there are no reservations!");
		
		
		List<ReservationDetails> newFoodDetails = addNewFoodReservasions.getReservasionsDetails();
		verifyAddRemoveFoodParams(newFoodDetails, true);
		
		
		for (ReservationDetails existingReservasion : allAvailablePickupWindows) {

			String existingReservationName = existingReservasion.getName();
			ReservationDetails reservasionToAddFood = this.foodOperationHelpers.findMatchingReservationDetailsByName(
					newFoodDetails, existingReservationName);

			if (reservasionToAddFood != null) {
				for (Food newFood : reservasionToAddFood.getFoodsToGive()) {
					if (!this.foodOperationHelpers.isFoodExist(existingReservasion.getFoodsToGive(), newFood.getType()))
						existingReservasion.getFoodsToGive().add(newFood);
				}
			}
		}

		return allReservations;
	}

	public void verifyAddRemoveFoodParams(List<ReservationDetails> newFoodDetails, boolean isNumOfDishesRequired) {
		for (ReservationDetails reservationToAddFood: newFoodDetails) {
			this.reservationInputVerifcation.verifyReservationName(reservationToAddFood.getName());
			this.reservationInputVerifcation.verifyFoodsParams(reservationToAddFood.getFoodsToGive(), isNumOfDishesRequired);
		}
	}

	public Reservations removeFood(Reservations allReservations, Reservations removeFoodReservasions) {
		/*
		 * Json example:
		 * 
		 * "operationAttributes":  {
       "reservasionsDetails": [ 
           {
               "name": "order1",
                "foodsToGive": [ 
                    {
                        "type": "chicken"
                    },
                    {
                        "type": "burger"
                    }  
                ]
           },
           {
                "name": "order2",
                "foodsToGive": [ 
                    {
                        "type": "banana"
                    },
                    {
                        "type": "nuts"
                    }
                ]
           }
       ]
   }
		 */
		List<ReservationDetails> allAvailablePickupWindows = allReservations.getReservasionsDetails();
		if (allAvailablePickupWindows == null || allAvailablePickupWindows.isEmpty()) 
			throw new BadRequest("cannot perform 'remove_donator_food' if there are no reservations!");
		
		List<ReservationDetails> allRemoveFoodReservationDetails = removeFoodReservasions.getReservasionsDetails();
		verifyAddRemoveFoodParams(allRemoveFoodReservationDetails, false);
		
		for (ReservationDetails existingReservasion : allAvailablePickupWindows) {

			ReservationDetails reservasionToRemoveFood = this.foodOperationHelpers.findMatchingReservationDetailsByName(
					allRemoveFoodReservationDetails, existingReservasion.getName());

			if (reservasionToRemoveFood != null) {
				for (Food oldFood : reservasionToRemoveFood.getFoodsToGive()) {

					String oldFoodType = oldFood.getType();
					
					Food foodToRemove = this.foodOperationHelpers.
							findMatchingFoodByType(existingReservasion.getFoodsToGive(), oldFoodType);

					if (foodToRemove != null) {
						existingReservasion.getFoodsToGive().remove(foodToRemove);
						for (Reciever existingReciver: existingReservasion.getRecievers()) {
		
							List<Food> recieverAvailableFoods = existingReciver.getFoodsToTake();
							Food recieverFoodToRemove = this.foodOperationHelpers.
									findMatchingFoodByType(recieverAvailableFoods, oldFoodType);		
							
							if (recieverFoodToRemove != null)
								recieverAvailableFoods.remove(foodToRemove);
						}
					}
				} 
			}
		}

		return allReservations;
	}
}
