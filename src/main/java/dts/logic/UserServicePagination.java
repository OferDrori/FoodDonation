package dts.logic;

import java.util.List;

import dts.boundaries.UserBoundary;

public interface UserServicePagination extends UsersService {
	
	public List<UserBoundary> getAllUsersWithPagination(String adminSpace, String adminEmail, int size, int page);
}
