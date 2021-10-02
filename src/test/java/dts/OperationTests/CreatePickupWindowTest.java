package dts.OperationTests;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

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
import dts.display.UserBoundaryRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CreatePickupWindowTest extends OperationTestUtils {

	private int port;
	private String url;
	private RestTemplate restTemplate;
	private UserApiTesting userTest;
	private ItemsApiTesting itemTest;
	private ItemBoundary currentItem;
	private OperationsApiTesting operationTest;

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
	public void testInvokeCreatePickupWindow() {
		UserBoundary userWithPlayerRole = createUserForOperationTests(UserTestConstants.PLAYER_ROLE);

		ItemBoundary createdItem = getItem(getUserSpace(userWithPlayerRole), getUserEmail(userWithPlayerRole),
				currentItem.getItemId().getSpace(), currentItem.getItemId().getId());

		OperationBoundary ob = createAGeneralOperationBoundary(userWithPlayerRole, createdItem);

		invokeCreatePickupWindowOperationOnItem(ob, OperationTestConstants.DONATOR_NAME,
				getPickupTimeForTheNextHoursFromNow(1, 3), OperationTestConstants.PICKUP_LOCATION,
				OperationTestConstants.FOOD_LIST, OperationTestConstants.RECEIVERS_LIST);

		createdItem = getItem(getUserSpace(userWithPlayerRole), getUserEmail(userWithPlayerRole),
				currentItem.getItemId().getSpace(), currentItem.getItemId().getId());

		assertPickupWindowOnItem(createdItem, OperationTestConstants.DONATOR_NAME,
				getPickupTimeForTheNextHoursFromNow(1, 3), OperationTestConstants.PICKUP_LOCATION,
				OperationTestConstants.FOOD_LIST, OperationTestConstants.RECEIVERS_LIST);
	}

	private void invokeCreatePickupWindowOperationOnItem(OperationBoundary ob, String donatorName,
			PickupTime pickupTime, PickupLocation pickupLocation, List<Food> foodList, List<Reciever> receiversList) {

		ob.setType(OperationTestConstants.CREATE_PICKUP_WINDOW);

		OperationBoundary createPickupWindowOp = packArgsToAnOperationBoundary(ob, donatorName, pickupTime,
				pickupLocation, foodList, receiversList);

		this.operationTest.invokeOperationOnItem(url, createPickupWindowOp);
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

	public void deleteAllUsers(String adminSpace, String adminEmail) {
		String deleteAllUsersUrl = this.makeUrl(UserTestConstants.DELETE_ALL_USERS_URL, adminSpace, adminEmail);
		this.userTest.deleteAllUsers(deleteAllUsersUrl, adminSpace, adminEmail);
	}

	public ItemBoundary createStandardItemForOperationTests() {
		UserBoundary manager = createUserForOperationTests(UserTestConstants.MANAGER_ROLE);

		ItemBoundary ib = createStandardItemBoundaryForTestingWhichWasCreatedBy(new CreatedBy(manager.getUserId()));

		String createItemUrl = makeUrl(ItemTestConstants.CREATE_ITEM_URL, getUserSpace(manager), getUserEmail(manager));
		return this.itemTest.createItem(createItemUrl, ib);
	}

	public UserBoundary createUserForOperationTests(UserBoundaryRole role) {
		return userTest.createUserForTests(role, url);
	}

	public ItemBoundary getItem(String space, String email, String itemSpace, String itemId) {
		String getItemUrl = makeUrl(ItemTestConstants.GET_ITEM_URL, space, email, itemSpace, itemId);
		return this.itemTest.getItem(getItemUrl, space, email, itemSpace, itemId);
	}

	public String makeUrl(String... values) {
		return super.makeUrl(this.url, values);
	}
}
