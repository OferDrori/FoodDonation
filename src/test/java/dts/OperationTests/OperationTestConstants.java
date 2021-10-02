package dts.OperationTests;

import java.util.ArrayList;
import java.util.List;

import dts.TestUtils.TestConstants;
import dts.display.Food;
import dts.display.PickupLocation;
import dts.display.PickupTime;
import dts.display.Reciever;

public class OperationTestConstants extends TestConstants {

	// Standard urls
	public static final String OPERATIONS_URL = "dts/operations";

	// API requests urls
	public static final String INVOKE_OPERATION_URL = OPERATIONS_URL;
	public static final String DELETE_ALL_OPERATIONS_URL = ADMIN_URL + "/operations";
	public static final String GET_ALL_OPERATIONS_URL = ADMIN_URL + "/operations";

	// Standard properties
	public static final String DONATOR_NAME = "donatorName";
	public static final PickupTime PICKUP_TIME = null;
	public static final PickupLocation PICKUP_LOCATION = new PickupLocation(1.6, 1.6);
	public static final List<Food> FOOD_LIST = getStandardFood();
	public static final String RECEIVER_NAME = "receiverName";
	public static final List<Reciever> RECEIVERS_LIST = null;

	// Food donator manager operations
	public static final String CREATE_PICKUP_WINDOW = "create_pickup_window";
	public static final String CANCEL_PICKUP_WINDOW = "cancel_pickup_window";
	public static final String UPDATE_PICKUP_WINDOW = "update_pickup_window";
	public static final String ADD_NEW_FOOD = "add_new_food";
	public static final String REMOVE_FOOD = "remove_food";

	// Food receiver player operations
	public static final String SCHEDULE_PICKUP = "schedule_pickup";
	public static final String CANCEL_SCHEDULE_PICKUP = "cancel_schedule_pickup";
	public static final String UPDATE_SCHEDULE_PICKUP = "update_schedule_pickup";

	public static final String AVAILABILITY_WINDOW = "AVAILABILITY_WINDOW";

	private static List<Food> getStandardFood() {
		ArrayList<Food> foods = new ArrayList<>();
		foods.add(new Food("Marshmallow", 3));
		foods.add(new Food("Chocolate", 4));
		return foods;
	}
}
