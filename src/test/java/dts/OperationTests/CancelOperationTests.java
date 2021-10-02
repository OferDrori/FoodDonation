package dts.OperationTests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import dts.Jackson;
import dts.ItemTests.ItemTestConstants;
import dts.ItemTests.ItemsApiTesting;
import dts.UserTests.UserApiTesting;
import dts.UserTests.UserTestConstants;
import dts.boundaries.ItemBoundary;
import dts.boundaries.OperationBoundary;
import dts.boundaries.UserBoundary;
import dts.display.CreatedBy;
import dts.display.Food;
import dts.display.PickupLocation;
import dts.display.PickupTime;
import dts.display.Reciever;
import dts.display.ReservationDetails;
import dts.display.Reservations;
import dts.display.UserBoundaryRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CancelOperationTests extends OperationTestUtils {

	private int port;
	private String url;
	private RestTemplate restTemplate;
	private UserApiTesting userTest;
	private ItemsApiTesting itemTest;
	private ItemBoundary currentItem;
	private OperationsApiTesting operationTest;
	private Jackson jackson;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port;
		this.restTemplate = new RestTemplate();
		this.itemTest = new ItemsApiTesting(this.restTemplate);
		this.userTest = new UserApiTesting(this.restTemplate);
		this.operationTest = new OperationsApiTesting(this.restTemplate);
		this.jackson = new Jackson(new ObjectMapper());
	}

	@BeforeEach
	public void setup() {
		/**
		 * We create a manager user with MANAGER role, so we could create an item to
		 * invoke an operation on.
		 */
		currentItem = createStandardItemForOperationTests();
	}

	@AfterEach
	public void tearDown() {
		/**
		 * We create an admin with ADMIN role and clear the DB.
		 * 
		 */

		UserBoundary admin = createUserForOperationTests(UserTestConstants.ADMIN_ROLE);
		deleteAllOperations(getUserSpace(admin), getUserEmail(admin));
		deleteAllItems(getUserSpace(admin), getUserEmail(admin));
		deleteAllUsers(getUserSpace(admin), getUserEmail(admin));
	}

	@Test
	public void contextLoads() {
		/**
		 * This test is a sanity test, which verifies that the server loads.
		 */
	}

	@Test
	public void testInvokeCancelPickupWindow() {
		UserBoundary userWithPlayerRole = createUserForOperationTests(UserTestConstants.PLAYER_ROLE);

		ItemBoundary createdItem = getItem(getUserSpace(userWithPlayerRole), getUserEmail(userWithPlayerRole),
				currentItem.getItemId().getSpace(), currentItem.getItemId().getId());

		OperationBoundary ob = createAGeneralOperationBoundary(userWithPlayerRole, createdItem);

		String create1Name = "pickupWindow1", create2Name = "pickupWindow2";
		PickupTime pickupTime = getPickupTimeForTheNextHoursFromNow(1, 3);

		invokeCreatePickupWindowOperationOnItem(ob, create1Name,
				pickupTime, OperationTestConstants.PICKUP_LOCATION, OperationTestConstants.FOOD_LIST,
				OperationTestConstants.RECEIVERS_LIST);

		invokeCreatePickupWindowOperationOnItem(ob, create2Name, pickupTime, OperationTestConstants.PICKUP_LOCATION,
				OperationTestConstants.FOOD_LIST, OperationTestConstants.RECEIVERS_LIST);

		invokeCancelPickupWindowOperationOnItem(ob, create2Name);

		createdItem = getItem(getUserSpace(userWithPlayerRole), getUserEmail(userWithPlayerRole),
				currentItem.getItemId().getSpace(), currentItem.getItemId().getId());

		assertCancelPickupWindowOperationOnItem(createdItem, create2Name, pickupTime,
				OperationTestConstants.PICKUP_LOCATION, OperationTestConstants.FOOD_LIST,
				OperationTestConstants.RECEIVERS_LIST);
	}

	private void invokeCancelPickupWindowOperationOnItem(OperationBoundary ob, String create2Name) {
		ob.setType(OperationTestConstants.CANCEL_PICKUP_WINDOW);
		OperationBoundary cancelPickupWindowOp = packArgsToAnOperationBoundary(ob, create2Name, null, null, null, null);
		this.operationTest.invokeOperationOnItem(url, cancelPickupWindowOp);
	}

	private void invokeCreatePickupWindowOperationOnItem(OperationBoundary ob, String donatorName,
			PickupTime pickupTime, PickupLocation pickupLocation, List<Food> foodList, List<Reciever> receiversList) {

		ob.setType(OperationTestConstants.CREATE_PICKUP_WINDOW);

		OperationBoundary createPickupWindowOp = packArgsToAnOperationBoundary(ob, donatorName, pickupTime,
				pickupLocation, foodList, receiversList);

		this.operationTest.invokeOperationOnItem(url, createPickupWindowOp);
	}

	private void assertCancelPickupWindowOperationOnItem(ItemBoundary itemBoundary, String donatorName,
			PickupTime pickupTime, PickupLocation pickupLocation, List<Food> foods, List<Reciever> receivers) {

		Reservations itemAttributes = this.jackson.fromMapToReservationsObject(itemBoundary.getItemAttributes());

		ReservationDetails detailsOfReservationWhichWeCancel = new ReservationDetails(donatorName, pickupTime,
				pickupLocation, foods, receivers);

		int index = getReservationDetailsIndex(itemAttributes.getReservasionsDetails(),
				detailsOfReservationWhichWeCancel);

		assertEquals(index, -1);
	}

	private int getReservationDetailsIndex(List<ReservationDetails> reservationsDetailsList,
			ReservationDetails detailsToMatch) {
		int index = -1;
		for (int i = 0; i < reservationsDetailsList.size(); i++) {
			ReservationDetails currentDetails = reservationsDetailsList.get(i);
			if (areReservationsDetailsMatch(currentDetails, detailsToMatch))
				index = i;
		}
		return index;
	}

	private boolean areReservationsDetailsMatch(ReservationDetails currentDetails, ReservationDetails detailsToMatch) {
		boolean doTheirNamesMatch = currentDetails.getName().equals(detailsToMatch.getName());

		boolean doTheirLatitudesMatch = currentDetails.getPickupLocation().getLat() == detailsToMatch
				.getPickupLocation().getLat();
		boolean doTheirLongitudesMatch = currentDetails.getPickupLocation().getLng() == detailsToMatch
				.getPickupLocation().getLng();

		boolean doTheirFoodsMatch = true;
		for (int i = 0; i < currentDetails.getFoodsToGive().size(); i++) {
			if (currentDetails.getFoodsToGive().get(i).getNumberOfDishes() != detailsToMatch.getFoodsToGive().get(i)
					.getNumberOfDishes())
				doTheirFoodsMatch = false;
			if (currentDetails.getFoodsToGive().get(i).getType() != detailsToMatch.getFoodsToGive().get(i).getType())
				doTheirFoodsMatch = false;
		}

		return doTheirNamesMatch && doTheirLatitudesMatch && doTheirLongitudesMatch && doTheirFoodsMatch;
	}

	public ItemBoundary createStandardItemForOperationTests() {
		UserBoundary manager = createUserForOperationTests(UserTestConstants.MANAGER_ROLE);

		ItemBoundary ib = createStandardItemBoundaryForTestingWhichWasCreatedBy(new CreatedBy(manager.getUserId()));

		String createItemUrl = makeUrl(ItemTestConstants.CREATE_ITEM_URL, getUserSpace(manager), getUserEmail(manager));
		return this.itemTest.createItem(createItemUrl, ib);
	}

	public ItemBoundary getItem(String space, String email, String itemSpace, String itemId) {
		String getItemUrl = makeUrl(ItemTestConstants.GET_ITEM_URL, space, email, itemSpace, itemId);
		return this.itemTest.getItem(getItemUrl, space, email, itemSpace, itemId);
	}

	public OperationBoundary[] getAllOperations(String adminSpace, String adminEmail) {
		String getAllOperationsUrl = makeUrl(OperationTestConstants.GET_ALL_OPERATIONS_URL, adminSpace, adminEmail);
		return this.operationTest.getAllOperations(getAllOperationsUrl, adminSpace, adminEmail);
	}

	public void deleteAllOperations(String adminSpace, String adminEmail) {
		String deleteAllOperationsUrl = this.makeUrl(OperationTestConstants.DELETE_ALL_OPERATIONS_URL, adminSpace,
				adminEmail);
		this.operationTest.deleteAllOperations(deleteAllOperationsUrl, adminSpace, adminEmail);
	}

	public void deleteAllItems(String adminSpace, String adminEmail) {
		String deleteAllItemsUrl = this.makeUrl(ItemTestConstants.DELETE_ALL_ITEMS_URL, adminSpace, adminEmail);
		this.restTemplate.delete(deleteAllItemsUrl, adminSpace, adminEmail);
	}

	private UserBoundary createUserForOperationTests(UserBoundaryRole playerRole) {
		return this.userTest.createUserForTests(playerRole, url);
	}

	public void deleteAllUsers(String adminSpace, String adminEmail) {
		String deleteAllUsersUrl = this.makeUrl(UserTestConstants.DELETE_ALL_USERS_URL, adminSpace, adminEmail);
		this.userTest.deleteAllUsers(deleteAllUsersUrl, adminSpace, adminEmail);
	}

	public String makeUrl(String... values) {
		return super.makeUrl(this.url, values);
	}
}