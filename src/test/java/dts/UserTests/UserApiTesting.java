package dts.UserTests;


import org.springframework.web.client.RestTemplate;

import dts.TestUtils.TestUtils;
import dts.boundaries.UserBoundary;
import dts.display.NewUserDetails;
import dts.display.UserBoundaryRole;

public class UserApiTesting extends TestUtils{
	
	private RestTemplate restTemplate;

	public UserApiTesting(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public UserBoundary getUser(String url, String userSpace, String userEmail) {
		return this.restTemplate.getForObject(url, UserBoundary.class, userSpace, userEmail);
	}
	
	public UserBoundary createUser(String url, NewUserDetails newUserRequest) {
		return this.restTemplate.postForObject(url, newUserRequest, UserBoundary.class);
	}
	
	public void updateUser(String url, NewUserDetails updateUserRequest) {
		this.restTemplate.put(url, updateUserRequest);
	}
	
	public UserBoundary[] getAllUsers(String url, String adminSpace, String adminEmail) {
		return this.restTemplate.getForObject(url, UserBoundary[].class, adminSpace, adminEmail);
	}
	
	public void deleteAllUsers(String url, String adminSpace, String adminEmail) {
		this.restTemplate.delete(url, adminSpace, adminEmail);
	}
	public UserBoundary createUserForTests(UserBoundaryRole role, String url) {
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
		String createUserUrl = url + UserTestConstants.CREATE_USER_URL;
		return createUser(createUserUrl, userDetails);
	}

}
