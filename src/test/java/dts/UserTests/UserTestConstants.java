package dts.UserTests;

import dts.TestUtils.TestConstants;
import dts.display.UserBoundaryRole;

public class UserTestConstants extends TestConstants{
	
	// User unprivilleged attributes
	public static final String AVATAR = "avatar";
	public static final String USERNAME = "username";
	public static final UserBoundaryRole PLAYER_ROLE = UserBoundaryRole.PLAYER;
	public static final UserBoundaryRole MANAGER_ROLE = UserBoundaryRole.MANAGER;

	// User privileged attribtues
	public static final UserBoundaryRole ADMIN_ROLE = UserBoundaryRole.ADMIN;

	// Emails
	public static final String ADMIN_EMAIL = "Admin@walla.com";
	public static final String MANAGER_EMAIL = "Manager@walla.com";
	public static final String PLAYER_EMAIL = "Player@walla.com";

	// Standard urls
	public static final String USER_URL = "dts/users";

	// API requests urls
	public static final String CREATE_USER_URL = USER_URL;
	public static final String UPDATE_USER_URL = USER_URL; // + "/{userSpace}/{userEmail}";
	public static final String GET_USER_URL = USER_URL + "/login"; // + {userSpace}/{userEmail}";
	public static final String DELETE_ALL_USERS_URL = ADMIN_URL + "/users"; // + {adminSpace}/{adminEmail}";
	public static final String GET_ALL_USERS_URL = ADMIN_URL + "/users"; // + {adminSpace}/{adminEmail}";

	// User roles
	public static UserBoundaryRole[] USER_ROLES = { UserTestConstants.PLAYER_ROLE, UserTestConstants.ADMIN_ROLE,
			UserTestConstants.MANAGER_ROLE };

}
