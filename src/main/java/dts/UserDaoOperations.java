package dts;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dts.data.UserEntity;
import dts.display.UserRole;
import dts.errors.UnAuthorizedError;
import dts.errors.UserNotFound;
import dts.logic.UserDao;

@Component
public class UserDaoOperations {

	private UserDao userDao;

	@Autowired
	public UserDaoOperations(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * Given a user space and user email, get the UserEntity object from the DB.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param useremail:
	 *            user email.
	 * 
	 * @exception UserNotFound:
	 *                In case the user was not found in the DB
	 * @exception UnAuthorizedError
	 *                in case the user tries to do a login and fails.
	 * 
	 * @return UserEntity object if it was found in the DB
	 */
	public UserEntity GetUserFromDB(String space, String email, boolean isLoginRequired) {
		Optional<UserEntity> userEntity = this.userDao.findById(space + ":" + email);
		if (userEntity.isPresent())
			return userEntity.get();
		else {
			if (isLoginRequired)
				throw new UnAuthorizedError(
						"user space: " + space + " user email: " + email + " is not authorized to login!");
			else
				throw new UserNotFound("User space: " + space + ", User email: " + email + " was not found!");
		}

	}

	/**
	 * Given a user space, email and role, verifies if the role is as expected.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param useremail:
	 *            user email.
	 * @param role:
	 *            user role.
	 * 
	 * @exception UserNotFound:
	 *                In case the user was not found in the DB
	 * 
	 * @return UserEntity object if it was found in the DB
	 */
	public boolean isUserRoleAsExcpected(String space, String email, UserRole role) {
		return this.GetUserFromDB(space, email, false).getRole() == role;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
