package dts.logic;

import java.util.List;

import dts.boundaries.UserBoundary;

/**
 * A user service interface that defines all the user api functionality.
 */
public interface UsersService {
	public UserBoundary createUser(UserBoundary user);

	public  UserBoundary login(String userSpace, String userEmail);

	public  UserBoundary updateUser(String userSpace, String userEmail, UserBoundary update) throws Exception;

	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail);

	public void deleteAllUsers(String adminSpace, String adminEmail);
}
