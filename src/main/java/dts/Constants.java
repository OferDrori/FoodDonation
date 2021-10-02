package dts;

public interface Constants {
	// Food donator manager operations
	public static final String CREATE_PICKUP_WINDOW = "create_pickup_window";
	public static final String CANCEL_PICKUP_WINDOW = "cancel_pickup_window";
	public static final String UPDATE_PICKUP_WINDOW = "update_pickup_window";
	public static final String ADD_NEW_DONATOR_FOOD = "add_new_donator_food";
	public static final String REMOVE_DONATOR_FOOD = "remove_donator_food";

	// Food receiver player operations
	public static final String SCHEDULE_PICKUP = "schedule_pickup";
	public static final String CANCEL_SCHEDULE_PICKUP = "cancel_schedule_pickup";
	public static final String FOOD_DELIVERED = "food_delivered";
	
	public static final String DONATOR = "donator";
	public static final String RECIEVER = "reciever";
}
