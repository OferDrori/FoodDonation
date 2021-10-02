package dts.ItemTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import dts.TestUtils.TestUtils;
import dts.UserTests.UserApiTesting;
import dts.UserTests.UserTestConstants;
import dts.boundaries.ItemBoundary;
import dts.boundaries.UserBoundary;
import dts.display.CreatedBy;
import dts.display.ItemId;
import dts.display.Location;
import dts.display.NewUserDetails;
import dts.display.UserBoundaryRole;
import dts.display.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ItemTests extends TestUtils {

	private int port;
	private String url;
	private RestTemplate restTemplate;
	private ItemsApiTesting itemTest;
	private UserApiTesting userTest;

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
	}

	@Test
	public void contextLoads() {
		/**
		 * This test is a sanity test, which verifies that the server loads.
		 */
	}

	@AfterEach
	public void tearDown() {
		UserBoundary admin = createUserForItemsTests(UserTestConstants.ADMIN_ROLE);

		deleteAllItems(getUserSpace(admin), getUserEmail(admin));
	}

	public ItemBoundary createStandardItemBoundaryForTestingWhichWasCreatedBy(CreatedBy createdByUser) {
		return new ItemBoundary(null, ItemTestConstants.TYPE, ItemTestConstants.NAME, ItemTestConstants.ACTIVE, null,
				createdByUser, ItemTestConstants.LOCATION, ItemTestConstants.NULL_ITEM_ATTRIBUTES);
	}

	public void assertItemDetalis(ItemBoundary itemBoundary, ItemId itemId, String type, String name, boolean active,
			CreatedBy createdBy, Location location) {

		assertThat(itemBoundary).isNotNull();
		assertEquals(itemBoundary.getItemId().getSpace(), ItemTestConstants.ITEM_SPACE);
		assertEquals(itemBoundary.getType(), type);
		assertEquals(itemBoundary.getName(), name);
		assertEquals(itemBoundary.getActive(), active);
		assertThat(itemBoundary.getCreatedTimestamp().getTime() > new Date().getTime());
		assertEquals(itemBoundary.getCreatedBy().getUserId().getSpace(), createdBy.getUserId().getSpace());
		assertEquals(itemBoundary.getCreatedBy().getUserId().getEmail(), createdBy.getUserId().getEmail());
		assertEquals(itemBoundary.getLocation().getLat(), location.getLat());
		assertEquals(itemBoundary.getLocation().getLng(), location.getLng());
	}

	@Test
	public void testCreateItem() {

		/**
		 * This test tests that the regular Item creation passes.
		 */

		UserBoundary mananger = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		ItemBoundary itemBoundary = new ItemBoundary(null, ItemTestConstants.TYPE, ItemTestConstants.NAME,
				ItemTestConstants.ACTIVE, null, new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
				ItemTestConstants.NULL_ITEM_ATTRIBUTES);

		itemBoundary = createItem(getUserSpace(mananger), getUserEmail(mananger), itemBoundary);

		assertItemDetalis(itemBoundary, new ItemId(), ItemTestConstants.TYPE, ItemTestConstants.NAME,
				ItemTestConstants.ACTIVE, new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION);
	}

	@Test
	public void testCreateItemNullAttributesNegative() {

		/**
		 * This test makes sure that each attribute that shouldn't be null would cause
		 * an exception if a user (manager) tries to create an item with a null field
		 * instead of it. There are 3 exceptions (we allow null fields to be sent from 3
		 * variables):
		 * 
		 * ItemId: no Item was created it, so there is no reason for it to have one.
		 * 
		 * createdBy: we allow the users to send a null value as the server ignores the
		 * createdBy field anyway, it just takes the parameter from the path.
		 * 
		 * createdTimestamp: we allow the users to send a null value (as it is tiring to
		 * try to write it in the correct format). The server will just generate the
		 * current time anyway.
		 * 
		 * ItemAttributes: if the user doesn't want any other attributes to be
		 * sent/embedded with the item, that's ok with us.
		 * 
		 * Afterwards we test that if these attributes will be null in an Item creation,
		 * an exception will not be thrown.
		 */

		UserBoundary mananger = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		ItemBoundary[] negativeItemBoundaries = {

				// Null type
				new ItemBoundary(ItemTestConstants.ITEM_ID, null, ItemTestConstants.NAME, ItemTestConstants.ACTIVE,
						new Date(), new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES),

				// Null name
				new ItemBoundary(ItemTestConstants.ITEM_ID, ItemTestConstants.TYPE, null, ItemTestConstants.ACTIVE,
						new Date(), new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES),

				// Null active
				new ItemBoundary(ItemTestConstants.ITEM_ID, ItemTestConstants.TYPE, ItemTestConstants.NAME, null,
						new Date(), new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES),

				// Null location
				new ItemBoundary(ItemTestConstants.ITEM_ID, ItemTestConstants.TYPE, ItemTestConstants.NAME,
						ItemTestConstants.ACTIVE, new Date(), new CreatedBy(mananger.getUserId()), null,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES) };

		for (ItemBoundary negativeItemBoundary : negativeItemBoundaries) {
			assertThrows(RuntimeException.class,
					() -> createItem(getUserSpace(mananger), getUserEmail(mananger), negativeItemBoundary));
		}

		// Testing that if the mentioned attributes will be null, the creation will not
		// cause an exception
		ItemBoundary[] positiveItemBoundaries = {
				// Null ItemId
				new ItemBoundary(null, ItemTestConstants.TYPE, ItemTestConstants.NAME, ItemTestConstants.ACTIVE,
						new Date(), new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES),

				// Null createdTimestamp
				new ItemBoundary(ItemTestConstants.ITEM_ID, ItemTestConstants.TYPE, ItemTestConstants.NAME,
						ItemTestConstants.ACTIVE, null, new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES),

				// Null ItemAttributes
				new ItemBoundary(ItemTestConstants.ITEM_ID, ItemTestConstants.TYPE, ItemTestConstants.NAME,
						ItemTestConstants.ACTIVE, new Date(), new CreatedBy(mananger.getUserId()),
						ItemTestConstants.LOCATION, null), };

		for (ItemBoundary positiveItemBoundary : positiveItemBoundaries) {
			ItemBoundary createdPositiveItemBoundary = createItem(getUserSpace(mananger), getUserEmail(mananger),
					positiveItemBoundary);

			assertItemDetalis(createdPositiveItemBoundary, new ItemId(), ItemTestConstants.TYPE, ItemTestConstants.NAME,
					ItemTestConstants.ACTIVE, new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION);
		}
	}

	@Test
	public void testCreateItemEmptyAttributesNegative() {

		/**
		 * This test makes sure that each attribute that shouldn't be empty would cause
		 * an exception if a user (manager) tries to create an item with an empty field
		 * instead of it.
		 * 
		 * There are 2 attributes that should not be empty:
		 * 
		 * type: the Item type.
		 * 
		 * name: the item name.
		 */

		UserBoundary mananger = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		ItemBoundary[] negativeItemBoundaries = {

				// Empty type
				new ItemBoundary(ItemTestConstants.ITEM_ID, "", ItemTestConstants.NAME, ItemTestConstants.ACTIVE,
						new Date(), new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES),

				// Empty name
				new ItemBoundary(ItemTestConstants.ITEM_ID, ItemTestConstants.TYPE, "", ItemTestConstants.ACTIVE,
						new Date(), new CreatedBy(mananger.getUserId()), ItemTestConstants.LOCATION,
						ItemTestConstants.NULL_ITEM_ATTRIBUTES) };

		for (ItemBoundary negativeItemBoundary : negativeItemBoundaries) {
			assertThrows(RuntimeException.class,
					() -> createItem(getUserSpace(mananger), getUserEmail(mananger), negativeItemBoundary));
		}
	}

	@Test
	public void testCreateItemWithDifferentUserRolesNegative() {
		/**
		 * This test tests that Item creation will cause an exception if it will be
		 * committed by a non MANAGER user role, and that it would not cause an
		 * exception if it would be committed from a MANAGER user role.
		 */

		for (UserBoundaryRole userRole : UserTestConstants.USER_ROLES) {

			if (userRole == UserTestConstants.MANAGER_ROLE)
				continue;

			UserBoundary createdUser = createUserForItemsTests(userRole);

			ItemBoundary itemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(
					new CreatedBy(createdUser.getUserId()));

			assertThrows(RuntimeException.class,
					() -> createItem(getUserSpace(createdUser), getUserEmail(createdUser), itemBoundary));
		}
	}

	@Test
	public void testGetItem() {
		/**
		 * This test tests the regular GETting of an item.
		 */

		// Create the manager
		UserBoundary defaultManager = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		// Create the ItemBoundary
		ItemBoundary itemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(
				new CreatedBy(defaultManager.getUserId()));

		// Create the Item
		ItemBoundary createdItemBoundary = createItem(getUserSpace(defaultManager), getUserEmail(defaultManager),
				itemBoundary);
		String itemId = createdItemBoundary.getItemId().getId();

		// For each UserBoundaryRole - we check that we can GET the item
		for (UserBoundaryRole userRole : UserTestConstants.USER_ROLES) {

			if (userRole == UserTestConstants.ADMIN_ROLE)
				continue;

			UserBoundary defaultUser = createUserForItemsTests(userRole);

			itemBoundary = getItem(getUserSpace(defaultUser), getUserEmail(defaultUser), ItemTestConstants.ITEM_SPACE,
					itemId);

			assertItemDetalis(itemBoundary, new ItemId(), ItemTestConstants.TYPE, ItemTestConstants.NAME,
					ItemTestConstants.ACTIVE, new CreatedBy(defaultManager.getUserId()), ItemTestConstants.LOCATION);
		}
	}

	@Test
	public void testUpdateItem() {
		/**
		 * This test tests updating an item with valid scenarios.
		 */

		// Basic manager
		UserBoundary manager = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		// Basic Item
		ItemBoundary updatedItemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(
				new CreatedBy(manager.getUserId()));

		// Create the Item itself
		ItemBoundary createdItem = createItem(getUserSpace(manager), getUserEmail(manager), updatedItemBoundary);

		updatedItemBoundary.setItemId(createdItem.getItemId());

		// List of (positive/allowed) attributes we update
		String typeToUpdate = "otherType";
		String nameToUpdate = "otherName";
		boolean activeToUpdate = false;
		Location locationToUpdate = new Location(1.9, 1.9);

		// We update the ItemBoundary
		updatedItemBoundary = updateItemBoundary(updatedItemBoundary, typeToUpdate, nameToUpdate, activeToUpdate,
				locationToUpdate);

		// We update the Item
		updateItem(manager, updatedItemBoundary);

		createdItem = getItem(manager.getUserId().getSpace(), manager.getUserId().getEmail(),
				createdItem.getItemId().getSpace(), createdItem.getItemId().getId());

		assertItemDetalis(createdItem, new ItemId(), typeToUpdate, nameToUpdate, activeToUpdate,
				updatedItemBoundary.getCreatedBy(), locationToUpdate);

	}

	@Test
	public void testUpdateItemAttributesNegative() {
		/**
		 * This test tests that an attempt to update different attributes that are not
		 * supposed to be updated will not update them (it will just return the same
		 * object).
		 */

		// Basic manager
		UserBoundary manager = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		// Creating a common CreatedBy
		CreatedBy createdByUser = new CreatedBy(manager.getUserId());

		// Creating a basic ItemBoundary
		ItemBoundary basicItemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(createdByUser);

		// Creating the item
		ItemBoundary createdItem = createItem(getUserSpace(manager), getUserEmail(manager), basicItemBoundary);

		// Setting the wrong attributes to update
		ItemBoundary createdByUpdate = createStandardItemBoundaryForTestingWhichWasCreatedBy(createdByUser);
		createdByUpdate.setCreatedBy(new CreatedBy(new UserId(UserTestConstants.USER_SPACE, "bogusEmail@gmail.com")));

		ItemBoundary createdTimestampUpdate = createStandardItemBoundaryForTestingWhichWasCreatedBy(createdByUser);
		createdTimestampUpdate.setCreatedTimestamp(new Date());

		// Adding the negative ItemBoundaries to the list
		List<ItemBoundary> negativeItemBoundaryList = new ArrayList<>();
		Collections.addAll(negativeItemBoundaryList, createdByUpdate, createdTimestampUpdate);

		for (ItemBoundary itemBoundary : negativeItemBoundaryList) {

			// We set the same ItemId of createdItem for the update to find it
			itemBoundary.setItemId(createdItem.getItemId());

			updateItem(manager, itemBoundary);

			itemBoundary = getItem(getUserSpace(manager), getUserEmail(manager), itemBoundary.getItemId().getSpace(),
					itemBoundary.getItemId().getId());

			// We assertItemDetalis with the standard attributes (the same as createdItem)
			assertItemDetalis(itemBoundary, new ItemId(), ItemTestConstants.TYPE, ItemTestConstants.NAME,
					ItemTestConstants.ACTIVE, createdByUser, ItemTestConstants.LOCATION);
		}
	}

	@Test
	public void testUpdateItemFromInvalidUserRolesNegative() {
		/**
		 * This test tests that an attempt to update an item from different user roles
		 * that are not supposed to be able to update will throw an exception. Updating
		 * an Item from user roles which are supposed to be able to update is tested in
		 * a different test.
		 */

		// Declaring the negative User roles
		UserBoundaryRole[] negativeUserRoles = { UserTestConstants.ADMIN_ROLE, UserTestConstants.PLAYER_ROLE };

		// Creating a manager, which has a role that allow creating an item
		UserBoundary manager = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		// Creating the ItemBoundary
		ItemBoundary itemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(
				new CreatedBy(manager.getUserId()));

		// Creating the Item itself
		ItemBoundary basicItemBoundary = createItem(getUserSpace(manager), getUserEmail(manager), itemBoundary);

		for (UserBoundaryRole userRole : negativeUserRoles) {

			// Every time we create a user which has no permissions to update an item and
			// we check if updating with that user throws an exception
			UserBoundary negativeUser = createUserForItemsTests(userRole);

			assertThrows(RuntimeException.class, () -> updateItem(negativeUser, basicItemBoundary));
		}
	}

	@Test
	public void testUpdateItemsAttributesToNullNegative() {
		/**
		 * This test tests that an attempt to update different attributes that are not
		 * supposed to be updated to null will not update them (it will just return the
		 * same object). The 2 Attributes are: name, type
		 */

		// Basic manager
		UserBoundary manager = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		// Creating a basic ItemBoundary
		ItemBoundary basicItemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(
				new CreatedBy(manager.getUserId()));

		// Creating the item
		ItemBoundary createdItem = createItem(getUserSpace(manager), getUserEmail(manager), basicItemBoundary);

		// We set the same ItemId of createdItem for the update to find it
		basicItemBoundary.setItemId(createdItem.getItemId());

		// For each item which is not supposed to be updated to null, we update it, get
		// it and assert it

		// Name
		basicItemBoundary.setName(null);

		updateItem(manager, basicItemBoundary);

		basicItemBoundary = getItem(getUserSpace(manager), getUserEmail(manager),
				basicItemBoundary.getItemId().getSpace(), basicItemBoundary.getItemId().getId());

		assertItemDetalis(basicItemBoundary, new ItemId(), ItemTestConstants.TYPE, ItemTestConstants.NAME,
				ItemTestConstants.ACTIVE, new CreatedBy(manager.getUserId()), ItemTestConstants.LOCATION);

		// Type
		basicItemBoundary.setType(null);

		updateItem(manager, basicItemBoundary);

		basicItemBoundary = getItem(getUserSpace(manager), getUserEmail(manager),
				basicItemBoundary.getItemId().getSpace(), basicItemBoundary.getItemId().getId());

		assertItemDetalis(basicItemBoundary, new ItemId(), ItemTestConstants.TYPE, ItemTestConstants.NAME,
				ItemTestConstants.ACTIVE, new CreatedBy(manager.getUserId()), ItemTestConstants.LOCATION);
	}

	@Test
	public void testGetAllItems() {
		/**
		 * This test tests that getAllItems get the right amount of items and the
		 * correct ones.
		 */
		// Basic manager
		UserBoundary manager = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		// List to hold all the created Items
		List<ItemBoundary> createdItemsArrayList = new ArrayList<>();

		// Generate "load"
		for (int i = 0; i < 10; i++) {
			// Creating a basic first ItemBoundary
			ItemBoundary basicItemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(
					new CreatedBy(manager.getUserId()));

			// Creating the first item
			ItemBoundary itemBoundary = createItem(getUserSpace(manager), getUserEmail(manager), basicItemBoundary);

			createdItemsArrayList.add(itemBoundary);

			// Making sure the length is equal to the created Items loop variable - i
			assertEquals(i + 1, getAllItems(getUserSpace(manager), getUserEmail(manager)).length);
		}

		// Create another Item which has a different Name
		ItemBoundary differentNameItemBoundary = new ItemBoundary(null, ItemTestConstants.TYPE, "differentName",
				ItemTestConstants.ACTIVE, null, new CreatedBy(manager.getUserId()), ItemTestConstants.LOCATION, null);

		ItemBoundary differentNameItem = createItem(getUserSpace(manager), getUserEmail(manager),
				differentNameItemBoundary);

		createdItemsArrayList.add(differentNameItem);

		// Create another Item which has a different Type
		ItemBoundary differentTypeItemBoundary = new ItemBoundary(null, ItemTestConstants.TYPE, "differentName",
				ItemTestConstants.ACTIVE, null, new CreatedBy(manager.getUserId()), ItemTestConstants.LOCATION, null);

		ItemBoundary differentTypeItem = createItem(getUserSpace(manager), getUserEmail(manager),
				differentTypeItemBoundary);

		createdItemsArrayList.add(differentTypeItem);

		sortByNameAndThenById(createdItemsArrayList);

		// Converting the Arraylist to an Array
		ItemBoundary[] createdItems = new ItemBoundary[createdItemsArrayList.size()];
		createdItemsArrayList.toArray(createdItems);

		// Get all items
		ItemBoundary[] gettedItems = getAllItems(getUserSpace(manager), getUserEmail(manager));

		// asserting all Items
		assertItemArrays(createdItems, gettedItems);
	}

	public void sortByNameAndThenById(List<ItemBoundary> createdItemsArrayList) {
		Collections.sort(createdItemsArrayList, new Comparator<ItemBoundary>() {

			@Override
			public int compare(ItemBoundary ib1, ItemBoundary ib2) {
				if (ib1.getName().compareTo(ib2.getName()) == 0)
					return ib1.getItemId().getId().compareTo(ib2.getItemId().getId());
				return ib1.getName().compareTo(ib2.getName());
			}
		});
	}

	public void assertItemArrays(ItemBoundary[] items1, ItemBoundary[] items2) {
		assertEquals(items1.length, items2.length);

		int i = 0;
		for (ItemBoundary item : items1) {
			assertItemDetalis(item, null, items2[i].getType(), items2[i].getName(), items2[i].getActive(),
					items2[i].getCreatedBy(), items2[i].getLocation());
			i++;
		}
	}

	@Test
	public void testDeleteAllItems() {
		// Basic admin
		UserBoundary admin = createUserForItemsTests(UserTestConstants.ADMIN_ROLE);

		// Basic manager
		UserBoundary manager = createUserForItemsTests(UserTestConstants.MANAGER_ROLE);

		// Sanity check - deleting an empty DB
		deleteAllItems(getUserSpace(admin), getUserEmail(admin));

		assertEquals(0, getAllItems(getUserSpace(manager), getUserEmail(manager)).length);

		int loadSize = 10;

		// Creating a "load" to delete
		for (int i = 0; i < loadSize; i++) {
			ItemBoundary basicItemBoundary = createStandardItemBoundaryForTestingWhichWasCreatedBy(
					new CreatedBy(manager.getUserId()));

			createItem(getUserSpace(manager), getUserEmail(manager), basicItemBoundary);
		}

		// Making sure the DB / DB size was changed
		assertEquals(loadSize, getAllItems(getUserSpace(manager), getUserEmail(manager)).length);

		deleteAllItems(getUserSpace(admin), getUserEmail(admin));

		// Making sure the DB was cleared
		assertEquals(0, getAllItems(getUserSpace(manager), getUserEmail(manager)).length);

	}

	public String getUserSpace(UserBoundary userWhichHasASpace) {
		return userWhichHasASpace.getUserId().getSpace();
	}

	public String getUserEmail(UserBoundary userWhichHasAnEmail) {
		return userWhichHasAnEmail.getUserId().getEmail();
	}

	public ItemBoundary updateItemBoundary(ItemBoundary itemBoundaryToUpdate, String typeToUpdate, String nameToUpdate,
			boolean activeToUpdate, Location locationToUpdate) {
		itemBoundaryToUpdate.setType(typeToUpdate);
		itemBoundaryToUpdate.setName(nameToUpdate);
		itemBoundaryToUpdate.setActive(activeToUpdate);
		itemBoundaryToUpdate.setLocation(locationToUpdate);
		return itemBoundaryToUpdate;
	}

	public UserBoundary createUserForItemsTests(UserBoundaryRole role) {
		String email;
		switch (role) {
		case ADMIN:
			email = UserTestConstants.ADMIN_EMAIL;
			break;
		case MANAGER:
			email = UserTestConstants.MANAGER_EMAIL;
			break;
		default: // PLAYER
			email = UserTestConstants.PLAYER_EMAIL;
			break;
		}
		NewUserDetails userDetails = createNewUserDetails(UserTestConstants.AVATAR, email, UserTestConstants.USERNAME,
				role);
		String createUserUrl = this.url + UserTestConstants.CREATE_USER_URL;
		return userTest.createUser(createUserUrl, userDetails);
	}

	public UserBoundary createUser(String url, NewUserDetails newUserRequest) {
		return this.restTemplate.postForObject(url, newUserRequest, UserBoundary.class);
	}

	public ItemBoundary getItem(String space, String email, String itemSpace, String itemId) {
		String getItemUrl = makeUrl(ItemTestConstants.GET_ITEM_URL, space, email, itemSpace, itemId);
		return this.itemTest.getItem(getItemUrl, space, email, itemSpace, itemId);
	}

	public ItemBoundary createItem(String managerSpace, String managerEmail, ItemBoundary itemBoundary) {
		String createItemUrl = makeUrl(ItemTestConstants.CREATE_ITEM_URL, managerSpace, managerEmail);
		return this.itemTest.createItem(createItemUrl, itemBoundary);
	}

	public void updateItem(UserBoundary managerBoundary, ItemBoundary itemBoundaryToUpdate) {
		String updateUrl = makeUrl(ItemTestConstants.UPDATE_ITEM_URL, getUserSpace(managerBoundary),
				getUserEmail(managerBoundary), itemBoundaryToUpdate.getItemId().getSpace(),
				itemBoundaryToUpdate.getItemId().getId());
		this.itemTest.updateItem(updateUrl, itemBoundaryToUpdate);
	}

	public ItemBoundary[] getAllItems(String managerSpace, String managerEmail) {
		String getAllItemsUrl = makeUrl(ItemTestConstants.GET_ALL_ITEMS_URL, managerSpace, managerEmail);
		return this.itemTest.getAllItems(getAllItemsUrl, managerSpace, managerEmail);
	}

	public void deleteAllItems(String adminSpace, String adminEmail) {
		String deleteAllItemsUrl = this.makeUrl(ItemTestConstants.DELETE_ALL_ITEMS_URL, adminSpace, adminEmail);
		this.restTemplate.delete(deleteAllItemsUrl, adminSpace, adminEmail);
	}

	public String makeUrl(String... values) {
		return super.makeUrl(this.url, values);
	}
}