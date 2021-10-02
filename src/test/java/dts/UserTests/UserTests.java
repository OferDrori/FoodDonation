package dts.UserTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import dts.TestUtils.TestUtils;
import dts.boundaries.UserBoundary;
import dts.display.NewUserDetails;
import dts.display.UserBoundaryRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserTests extends TestUtils {

	private int port;
	private String url;
	private RestTemplate restTemplate;
	private UserApiTesting userTest;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port;
		this.restTemplate = new RestTemplate();
		this.userTest = new UserApiTesting(this.restTemplate);
	}

	@BeforeEach
	public void setup() {
		/*
		 * We create an admin user with admin roles, so we could clear the DB after
		 * every test run. If we don't create the admin, we will get an exception when
		 * we clear the db, as users with different roles can't clear the DB.
		 */

		NewUserDetails newUserWithAdminRole = createNewUserDetails(UserTestConstants.AVATAR,
				UserTestConstants.ADMIN_EMAIL, UserTestConstants.USERNAME, UserTestConstants.ADMIN_ROLE);
		this.createUser(newUserWithAdminRole);
	}

	@AfterEach
	public void tearDown() {
		// We duplicate the admin creation as some tests finish with an empty DB
		NewUserDetails newUserWithAdminRole = createNewUserDetails(UserTestConstants.AVATAR,
				UserTestConstants.ADMIN_EMAIL, UserTestConstants.USERNAME, UserTestConstants.ADMIN_ROLE);
		this.createUser(newUserWithAdminRole);

		// Deleting the DB
		String deleteAllUsersUrl = makeUrl(UserTestConstants.DELETE_ALL_USERS_URL, UserTestConstants.ADMIN_SPACE,
				UserTestConstants.ADMIN_EMAIL);
		this.userTest.deleteAllUsers(deleteAllUsersUrl, UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL);
	}

	@Test
	public void contextLoads() {
		/**
		 * This test is a sanity test, which verifies that the server loads.
		 */
	}

	public void assertUserDetalis(UserBoundary userBoundary, String avatar, String email, String userName,
			UserBoundaryRole userRole) {
		assertThat(userBoundary).isNotNull();
		assertThat(userBoundary.getAvatar()).isEqualTo(avatar);
		assertThat(userBoundary.getUserId().getEmail()).isEqualTo(email);
		assertThat(userBoundary.getUserId().getSpace()).isEqualTo(UserTestConstants.USER_SPACE);
		assertThat(userBoundary.getUsername()).isEqualTo(userName);
		assertThat(userBoundary.getRole()).isEqualTo(userRole);
	}

	public NewUserDetails createUserDetailsFromRole(UserBoundaryRole role) {
		String email = role == UserTestConstants.ADMIN_ROLE ? UserTestConstants.ADMIN_EMAIL
				: UserTestConstants.PLAYER_EMAIL;

		return createNewUserDetails(UserTestConstants.AVATAR, email, UserTestConstants.USERNAME, role);
	}

	@Test
	public void testCreateUserWithDifferentRoles() {

		/**
		 * This test tests User creation for every role.
		 */

		for (UserBoundaryRole userRole : UserTestConstants.USER_ROLES) {
			UserBoundary user = createUser(createUserDetailsFromRole(userRole));
			this.assertUserDetalis(user, user.getAvatar(), user.getUserId().getEmail(), user.getUsername(),
					user.getRole());
		}
	}

	@Test
	public void testCreateUserNullAttributesNegative() {

		/**
		 * This negative test tests that for every null attribute we get an exception.
		 */

		// Null avatar test
		testCreateUserNegative(null, UserTestConstants.PLAYER_EMAIL, UserTestConstants.USERNAME,
				UserTestConstants.PLAYER_ROLE);

		// Null email test
		testCreateUserNegative(UserTestConstants.AVATAR, null, UserTestConstants.USERNAME,
				UserTestConstants.PLAYER_ROLE);

		// Null username test
		testCreateUserNegative(UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL, null,
				UserTestConstants.PLAYER_ROLE);

		// Null UserBoundaryRole test
		testCreateUserNegative(UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL, UserTestConstants.USERNAME,
				null);
	}

	@Test
	public void testCreateUserEmptyAttributesNegative() {

		/**
		 * This negative test tests that for every empty attribute we get an exception.
		 * There isn't a test for an empty UserBoundaryRole as it is not a possible
		 * scenario (does not compile as well).
		 */

		// Empty avatar test
		testCreateUserNegative("", UserTestConstants.PLAYER_EMAIL, UserTestConstants.USERNAME,
				UserTestConstants.PLAYER_ROLE);

		// Empty email test
		testCreateUserNegative(UserTestConstants.AVATAR, "", UserTestConstants.USERNAME, UserTestConstants.PLAYER_ROLE);

		// Empty username test
		testCreateUserNegative(UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL, "",
				UserTestConstants.PLAYER_ROLE);

	}

	public void testCreateUserNegative(String avatar, String email, String username, UserBoundaryRole role) {
		assertThrows(RuntimeException.class, () -> {
			this.userTest.createUser(this.url + UserTestConstants.CREATE_USER_URL,
					createNewUserDetails(avatar, email, username, role));
		});
	}

	@Test
	public void testEmail() {

		/**
		 * This test tests some valid and invalid emails. Big credit to
		 * https://gist.github.com/cjaoude/fd9910626629b53c4d25 for the testing emails.
		 */

		String[] validEmails = { "email@example.com", "firstname.lastname@example.com", "email@subdomain.example.com",
				"firstname+lastname@example.com", "email@[123.123.123.123]", "1234567890@example.com",
				"email@example-one.com", "_______@example.com", "email@example.name", "email@example.museum",
				"email@example.co.jp", "firstname-lastname@example.com" };

		for (String validEmail : validEmails) {
			NewUserDetails newUserDetails = createNewUserDetails(UserTestConstants.AVATAR, validEmail,
					UserTestConstants.USERNAME, UserTestConstants.PLAYER_ROLE);
			UserBoundary createUserResult = this.userTest.createUser(this.url + UserTestConstants.CREATE_USER_URL,
					newUserDetails);
			this.assertUserDetalis(createUserResult, UserTestConstants.AVATAR, validEmail, UserTestConstants.USERNAME,
					UserTestConstants.PLAYER_ROLE);
		}
		String[] invalidEmails = { "plainaddres", "#@%^%#$@#$@#.com", "Joe Smith <email@example.com>",
				"email.example.com", "email@example@example.com", "email@example", "email@111.222.333.44444",
				"email@example..com", };

		for (String invalidEmail : invalidEmails) {
			assertThrows(RuntimeException.class, () -> {
				this.userTest.createUser(this.url + UserTestConstants.CREATE_USER_URL,
						createNewUserDetails(UserTestConstants.AVATAR, invalidEmail, UserTestConstants.USERNAME,
								UserTestConstants.PLAYER_ROLE));
			});
		}
	}

	@Test
	public void testGetUser() {

		/**
		 * This function tests that GETting a user works properly when the requested
		 * user exists (sanity check).
		 */

		// We create the user for the test
		this.userTest.createUser(this.url + UserTestConstants.CREATE_USER_URL,
				createNewUserDetails(UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL,
						UserTestConstants.USERNAME, UserTestConstants.PLAYER_ROLE));

		// We get the user that we just created
		String getUserUrl = makeUrl(UserTestConstants.GET_USER_URL, UserTestConstants.USER_SPACE,
				UserTestConstants.PLAYER_EMAIL);

		UserBoundary userGetResult = this.userTest.getUser(getUserUrl, UserTestConstants.USER_SPACE,
				UserTestConstants.PLAYER_EMAIL);

		assertUserDetalis(userGetResult, UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL,
				UserTestConstants.USERNAME, UserTestConstants.PLAYER_ROLE);
	}

	@Test
	public void testGetUserNegative() {

		/**
		 * This function tests that GETting a user throws an exception when the
		 * requested user does not exist.
		 */

		// Sanity check - getting the default admin user
		this.getUser(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL);

		assertThrows(RuntimeException.class, () -> {
			this.getUser("wrong path", UserTestConstants.ADMIN_EMAIL);
		});

		assertThrows(RuntimeException.class, () -> {
			this.getUser(UserTestConstants.ADMIN_SPACE, "wrong email");
		});

		assertThrows(RuntimeException.class, () -> {
			this.getUser("wrongPath", "wrong email");
		});

	}

	@Test
	public void testPrevillagesNegative() {
		/**
		 * This test checks that users with PLAYER_ROLE or MANAGER_ROLE cannot send GET
		 * requests that require ADMIN_ROLE role. It Also makes sure that users with
		 * ADMIN_ROLE can.
		 */

		UserBoundaryRole[] unprivillegedRoles = { UserTestConstants.PLAYER_ROLE, UserTestConstants.MANAGER_ROLE };
		UserBoundaryRole[] privillegedRoles = { UserTestConstants.ADMIN_ROLE };

		UserBoundary user;

		for (UserBoundaryRole unprivillegedRole : unprivillegedRoles) {
			user = createUser(createUserDetailsFromRole(unprivillegedRole));
			String space = user.getUserId().getSpace();
			String email = user.getUserId().getEmail();
			String getAllUsersUrl = this.makeUrl(UserTestConstants.GET_ALL_USERS_URL, space, email);
			assertThrows(RuntimeException.class, () -> {
				this.userTest.getAllUsers(getAllUsersUrl, space, email);
			});
		}

		for (UserBoundaryRole privillegedRole : privillegedRoles) {
			user = createUser(createUserDetailsFromRole(privillegedRole));
			String space = user.getUserId().getSpace();
			String email = user.getUserId().getEmail();
			String getAllUsersUrl = this.makeUrl(UserTestConstants.GET_ALL_USERS_URL, UserTestConstants.ADMIN_SPACE,
					UserTestConstants.ADMIN_EMAIL);
			this.userTest.getAllUsers(getAllUsersUrl, space, email);
		}
	}

	@Test
	public void testUpdateUser() {
		/**
		 * This test tests updating user with valid scenarios.
		 */

		String space, email;

		// There are no restrictions on username and avatar attributes but null and
		// empty which is tested on a different test
		String otherUsername = "otherUsername", otherAvatar = "otherAvatar";

		for (UserBoundaryRole role : UserTestConstants.USER_ROLES) {

			UserBoundary userToUpdate;

			if (role == UserTestConstants.ADMIN_ROLE) {
				space = UserTestConstants.ADMIN_SPACE;
				email = UserTestConstants.ADMIN_EMAIL;
				userToUpdate = createUser(createUserDetailsFromRole(UserTestConstants.ADMIN_ROLE));
			} else {
				space = UserTestConstants.USER_SPACE;
				email = UserTestConstants.PLAYER_EMAIL;
				userToUpdate = createUser(createUserDetailsFromRole(UserTestConstants.PLAYER_ROLE));
			}

			// Updating the user
			this.updateUser(otherAvatar, email, otherUsername, role, space);

			userToUpdate = this.getUser(space, email);

			// Testing the update was successful
			this.assertUserDetalis(userToUpdate, otherAvatar, email, otherUsername, role);
		}
	}

	public void testUpdateUserNegative() {

		/**
		 * This test tests that updating attributes to invalid values would cause an
		 * exception/error. We don't test email or space update as it is not supported.
		 */

		// Testing that unprivilleged user cannot update its attirbutes to invalid ones
		UserBoundary playerUserToUpdate = createUser(createUserDetailsFromRole(UserTestConstants.PLAYER_ROLE));

		String avatar = playerUserToUpdate.getAvatar();
		String username = playerUserToUpdate.getUsername();
		UserBoundaryRole role = playerUserToUpdate.getRole();

		assertThrows(RuntimeException.class, () -> {
			this.updateUser(null, UserTestConstants.PLAYER_EMAIL, username, role, UserTestConstants.USER_SPACE);
		});
		assertThrows(RuntimeException.class, () -> {
			this.updateUser(avatar, UserTestConstants.PLAYER_EMAIL, null, role, UserTestConstants.USER_SPACE);
		});
		assertThrows(RuntimeException.class, () -> {
			this.updateUser(null, UserTestConstants.PLAYER_EMAIL, username, role, UserTestConstants.USER_SPACE);
		});
	}

	@Test
	public void testUpdateUserNullAttributesNegative() {

		/**
		 * This negative test tests that for every attribute update to null would cause
		 * an exception. We don't check email update as it is not supported.
		 */

		// Null avatar test
		testUpdateUserNegative(null, UserTestConstants.PLAYER_EMAIL, UserTestConstants.USERNAME,
				UserTestConstants.PLAYER_ROLE, UserTestConstants.USER_SPACE);

		// Null username test
		testUpdateUserNegative(UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL, null,
				UserTestConstants.PLAYER_ROLE, UserTestConstants.USER_SPACE);

		// Null UserBoundaryRole test
		testUpdateUserNegative(UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL, UserTestConstants.USERNAME,
				null, UserTestConstants.USER_SPACE);
	}

	@Test
	public void testUpdateUserEmptyAttributesNegative() {

		/**
		 * This negative test tests that every attribute update to an empty value would
		 * cause an exception. There isn't a test for an empty UserBoundaryRole as it is
		 * not a possible scenario (does not compile as well). Also we don't check an
		 * update of the email attribute as it is not supported.
		 */

		// Empty avatar test
		testUpdateUserNegative("", UserTestConstants.PLAYER_EMAIL, UserTestConstants.USERNAME,
				UserTestConstants.PLAYER_ROLE, UserTestConstants.USER_SPACE);

		// Empty username test
		testUpdateUserNegative(UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL, "",
				UserTestConstants.PLAYER_ROLE, UserTestConstants.USER_SPACE);
	}

	public void testUpdateUserNegative(String avatar, String email, String username, UserBoundaryRole role,
			String space) {
		assertThrows(RuntimeException.class, () -> {
			this.updateUser(avatar, email, username, role, space);
		});
	}

	@Test
	public void testGetAllUsers() {

		/**
		 * This test tests that getAllUsers get the right amount of users and the
		 * correct ones. There is a default admin user which is created beforeEach test,
		 * and we create another user to see the change.
		 */

		// Getting the default admin user
		UserBoundary[] allUsers = this.getAllUsers(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL);
		UserBoundary defaultAdminUser = allUsers[0];
		this.assertUserDetalis(defaultAdminUser, UserTestConstants.AVATAR, UserTestConstants.ADMIN_EMAIL,
				UserTestConstants.USERNAME, UserTestConstants.ADMIN_ROLE);
		assertThat(1 == allUsers.length);

		// Creating another user in the DB
		createUser(createUserDetailsFromRole(UserTestConstants.PLAYER_ROLE));

		// Getting both
		allUsers = this.getAllUsers(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL);
		UserBoundary userWithPlayerRole = getTheFirstUserWithTheGivenRoleFromTheDB(UserTestConstants.PLAYER_ROLE);
		this.assertUserDetalis(userWithPlayerRole, UserTestConstants.AVATAR, UserTestConstants.PLAYER_EMAIL,
				UserTestConstants.USERNAME, UserTestConstants.PLAYER_ROLE);
		assertThat(2 == allUsers.length);

	}

	@Test
	public void testDeleteAllUsers() {

		// Sanity check - it supposed to be 1 as we create an admin before each test
		assertThat(1 == this.getAllUsers(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL).length);

		// Creating another user in the DB
		createUser(createUserDetailsFromRole(UserTestConstants.PLAYER_ROLE));

		// Creating another user in the DB
		createUser(createUserDetailsFromRole(UserTestConstants.MANAGER_ROLE));

		assertThat(3 == this.getAllUsers(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL).length);

		this.deleteAllUsers(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL);

		// We can't run another getAllUsers as we need an admin for it and we just
		// deleted all users in DB

		// Therefore, creating an admin in the DB
		createUser(createUserDetailsFromRole(UserTestConstants.ADMIN_ROLE));

		assertThat(1 == this.getAllUsers(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL).length);
	}

	public UserBoundary getTheFirstUserWithTheGivenRoleFromTheDB(UserBoundaryRole role) {
		for (UserBoundary userBoundary : getAllUsers(UserTestConstants.ADMIN_SPACE, UserTestConstants.ADMIN_EMAIL))
			if (userBoundary.getRole() == role)
				return userBoundary;

		return null;
	}

	public UserBoundary getUser(String space, String email) {
		String getUserUrl = makeUrl(UserTestConstants.GET_USER_URL, space, email);
		return this.userTest.getUser(getUserUrl, space, email);
	}

	public UserBoundary createUser(NewUserDetails userDetails) {
		String createUserUrl = this.url + UserTestConstants.CREATE_USER_URL;
		return this.userTest.createUser(createUserUrl, userDetails);
	}

	public void updateUser(String avatar, String email, String username, UserBoundaryRole role, String space) {
		NewUserDetails updateDetails = createNewUserDetails(avatar, email, username, role);
		String updateUrl = makeUrl(UserTestConstants.UPDATE_USER_URL, space, email);
		this.userTest.updateUser(updateUrl, updateDetails);
	}

	public UserBoundary[] getAllUsers(String adminSpace, String adminEmail) {
		String getAllUsersUrl = makeUrl(UserTestConstants.GET_ALL_USERS_URL, adminSpace, adminEmail);
		return this.userTest.getAllUsers(getAllUsersUrl, adminSpace, adminEmail);
	}

	public void deleteAllUsers(String adminSpace, String adminEmail) {
		String deleteAllUsersUrl = this.makeUrl(UserTestConstants.DELETE_ALL_USERS_URL, adminSpace, adminEmail);
		this.userTest.deleteAllUsers(deleteAllUsersUrl, adminSpace, adminEmail);
	}

	public String makeUrl(String... values) {
		return super.makeUrl(this.url, values);
	}
}
