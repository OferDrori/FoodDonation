package dts.ItemTests;

import java.util.Map;

import dts.TestUtils.TestConstants;
import dts.UserTests.UserTestConstants;
import dts.display.CreatedBy;
import dts.display.ItemId;
import dts.display.Location;
import dts.display.UserId;

public class ItemTestConstants extends TestConstants {

	// standard attributes
	public static final String ITEM_SPACE = "2021a.amit.kremer";
	public static final ItemId ITEM_ID = new ItemId(ITEM_SPACE, "1");
	public static final String TYPE = "DONATOR";
	public static final String NAME = "name";
	public static final boolean ACTIVE = true;
	public static final CreatedBy CREATED_BY = new CreatedBy(
			new UserId(UserTestConstants.USER_SPACE, UserTestConstants.PLAYER_EMAIL));
	public static final Location LOCATION = new Location(1.6, 1.6);
	public static final Map<String, Object> NULL_ITEM_ATTRIBUTES = null;

	// Standard Item attribtues after creation
	public static final String STANDARD_ITEM_ID = "1";

	// Standard urls
	public static final String ITEMS_URL = "dts/items";

	// API requests urls
	public static final String CREATE_ITEM_URL = ITEMS_URL;
	public static final String UPDATE_ITEM_URL = ITEMS_URL;
	public static final String GET_ITEM_URL = ITEMS_URL;
	public static final String DELETE_ALL_ITEMS_URL = ADMIN_URL + "/items";
	public static final String GET_ALL_ITEMS_URL = ITEMS_URL;
}
