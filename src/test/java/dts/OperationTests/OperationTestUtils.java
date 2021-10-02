package dts.OperationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import dts.Jackson;
import dts.ItemTests.ItemTestConstants;
import dts.TestUtils.TestUtils;
import dts.boundaries.ItemBoundary;
import dts.boundaries.OperationBoundary;
import dts.boundaries.UserBoundary;
import dts.display.CreatedBy;
import dts.display.Food;
import dts.display.InvokedBy;
import dts.display.Item;
import dts.display.PickupLocation;
import dts.display.PickupTime;
import dts.display.Reciever;
import dts.display.ReservationDetails;
import dts.display.Reservations;

public class OperationTestUtils extends TestUtils {

	private Jackson jackson;

	public OperationTestUtils() {
		this.jackson = new Jackson(new ObjectMapper());
	}

	protected OperationBoundary createAGeneralOperationBoundary(UserBoundary userBoundary, ItemBoundary itemBoundary) {
		OperationBoundary operationBoundary = new OperationBoundary();
		operationBoundary.setItem(new Item(itemBoundary.getItemId()));
		operationBoundary.setInvokedBy(new InvokedBy(userBoundary.getUserId()));

		return operationBoundary;
	}

	protected Map<String, Object> fromObjectToMap(Reservations createPickupWindow) {
		return this.jackson
				.fromStringToMapUsingJackson(this.jackson.fromObjectToStringUsingJackson(createPickupWindow));
	}

	protected Reservations fromMapToReservationsObject(Map<String, Object> attributes) {
		return this.jackson.fromMapToReservationsObject(attributes);
	}

	protected ItemBoundary createStandardItemBoundaryForTestingWhichWasCreatedBy(CreatedBy createdByUser) {
		return new ItemBoundary(null, ItemTestConstants.TYPE, ItemTestConstants.NAME, ItemTestConstants.ACTIVE, null,
				createdByUser, ItemTestConstants.LOCATION, ItemTestConstants.NULL_ITEM_ATTRIBUTES);
	}

	protected OperationBoundary packArgsToAnOperationBoundary(OperationBoundary operationBoundary, String donatorName,
			PickupTime pickupTime, PickupLocation pickupLocation, List<Food> foods, List<Reciever> receivers) {
		ReservationDetails details = new ReservationDetails(donatorName, pickupTime, pickupLocation, foods, receivers);

		Reservations createPickupWindow = new Reservations();
		createPickupWindow.setReservasionsDetails(Collections.singletonList(details));

		operationBoundary.setOperationAttributes(fromObjectToMap(createPickupWindow));
		return operationBoundary;
	}

	protected void assertReservationDetailsAttributes(ReservationDetails createdPickupWindowDetails,
			String expectedDonatorName, PickupTime expectedPickupTime, PickupLocation expectedPickupLocation,
			List<Food> expectedFoods) {

		String actualName = createdPickupWindowDetails.getName();
		PickupTime actualPickupTime = createdPickupWindowDetails.getPickupTime();
		PickupLocation actualPickupLocation = createdPickupWindowDetails.getPickupLocation();
		List<Food> actualFoods = createdPickupWindowDetails.getFoodsToGive();

		// Asserting the attributes are not null
		Object[] objectsToCheck = { actualName, actualPickupTime, actualPickupTime.getStart(),
				actualPickupTime.getEnd(), actualPickupLocation, actualPickupLocation.getLat(),
				actualPickupLocation.getLng(), actualFoods, actualFoods };

		for (Object object : objectsToCheck)
			assertThat(object).isNotNull();

		// Asserting that the attributes are equal to the createdPickupWindow
		assertEquals(expectedDonatorName, actualName);

		assertEquals(expectedPickupLocation.getLat(), actualPickupLocation.getLat());
		assertEquals(expectedPickupLocation.getLng(), actualPickupLocation.getLng());

		this.assertFoodsEqual(expectedFoods, actualFoods);
	}

	protected void assertPickupWindowOnItem(ItemBoundary itemBoundary, String donatorName, PickupTime pickupTime,
			PickupLocation pickupLocation, List<Food> foods, List<Reciever> receivers) {
		/**
		 * This methods asserts that the PickupWindow exists on the Item.
		 */

		Reservations itemAttributes = fromMapToReservationsObject(itemBoundary.getItemAttributes());

		ArrayList<ReservationDetails> listOfReservationDetails = (ArrayList<ReservationDetails>) itemAttributes
				.getReservasionsDetails();
		assertThat(listOfReservationDetails).isNotNull();
		assertThat(listOfReservationDetails).isNotEmpty();

		assertReservationDetailsAttributes(listOfReservationDetails.get(0), donatorName, pickupTime, pickupLocation,
				foods);
	}

	public String makeUrl(String url, String... values) {
		return super.makeUrl(url, values);
	}

}
