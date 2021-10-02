package dts;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import dts.boundaries.UserBoundary;
import dts.data.UserEntity;
import dts.display.UserBoundaryRole;
import dts.display.UserRole;
import dts.errors.BadRequest;
import dts.errors.BaseErrorClass;
import dts.errors.UnAuthorizedError;
import dts.logic.UserConverter;
import dts.logic.UserServicePagination;

@Service
public class RdbUserService implements UserServicePagination {
	private String userSpace;
	private UserDaoOperations userOperations;
	private UserConverter userConverter;
	private Utils utils;

	@Value("${spring.application.name}")
	public void setUserSpace(String userSpace) {
		this.userSpace = userSpace;
	}

	@Autowired
	public RdbUserService(UserDaoOperations userOperations, UserConverter userConverter) {
		this.userOperations = userOperations;
		this.userConverter = userConverter;
	}

	@PostConstruct
	public void init() {
		this.utils = new Utils();
	}

	/**
	 * Creates a new user and inserts it into the mockup DB.
	 *
	 * @param user:
	 *            a UserBoundary object of a new user to create.
	 * @return a UserBoundary object in case it's created successfully.
	 */
	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {

		validateNewUserBoundaryParams(user);

		// TODO: check if the next line is necessary
		user.getUserId().setSpace(this.userSpace);

		UserEntity newUserEntity = this.userConverter.toEntity(user);

		newUserEntity = this.userOperations.getUserDao().save(newUserEntity);

		return this.userConverter.toBoundary(newUserEntity);
	}

	/**
	 * Gets the specified user from the DB by user space and user email.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @return UserBoundary That was found in the Mockup DB.
	 */
	@Override
	public UserBoundary login(String userSpace, String userEmail) {
		return this.userConverter.toBoundary(this.userOperations.GetUserFromDB(userSpace, userEmail, true));
	}

	/**
	 * Updates the user in the Mockup DB by user space and user email.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param update:
	 *            UserBoundary object of which parameters to update.
	 * @return an updated object of UserBoundary.
	 */
	@Override

	public UserBoundary updateUser(String space, String userEmail, UserBoundary update) throws Exception {

		UserEntity updatedUserEntity = this.userOperations.GetUserFromDB(space, userEmail, false);

		if (!this.utils.isObjectNull(update.getAvatar()) && !this.utils.isStringEmpty(update.getAvatar())) {
			updatedUserEntity.setAvatar(update.getAvatar());
		}

		if (!this.utils.isObjectNull(update.getUsername()) && !this.utils.isStringEmpty(update.getUsername())) {
			updatedUserEntity.setUsername(update.getUsername());
		}

		if (!this.utils.isObjectNull(update.getRole())) {
			updatedUserEntity.setRole(UserRole.valueOf(update.getRole().name()));
		}

		if (!this.utils.isObjectNull(update.getUserId()) && !this.utils.isObjectNull(update.getUserId().getEmail())) {
			throw new BadRequest("Updating email is not supported!");
		}
		updatedUserEntity = this.userOperations.getUserDao().save(updatedUserEntity);

		return this.userConverter.toBoundary(updatedUserEntity);
	}

	/**
	 * Gets all the users from the DB by an admin space and admin email.
	 *
	 * @param adminSpace:
	 *            admin space.
	 * @param adminEmail:
	 *            admin email.
	 * 
	 * @exception UnAuthorizedError
	 *                in case the user is not an admin user.
	 * 
	 * @return list of UserBoundary that represents all the users that's currently
	 *         in the Mockup DB.
	 */
	@Override
	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail) {
		/*
		 * This method is not used but must be in the spec due to Eyal request, do not
		 * delete it!!
		 */
		throw new BaseErrorClass("deprecated");
	}

	/**
	 * Gets all the users from the DB by an admin space and admin email.
	 *
	 * @param adminSpace:
	 *            admin space.
	 * @param adminEmail:
	 *            admin email.
	 * 
	 * @exception UnAuthorizedError
	 *                in case the user is not an admin user.
	 * 
	 * @return list of UserBoundary that represents all the users that's currently
	 *         in the Mockup DB.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsersWithPagination(String adminSpace, String adminEmail, int size, int page) {

		if (this.userOperations.isUserRoleAsExcpected(adminSpace, adminEmail, UserRole.ADMIN)) {
			return this.userOperations.getUserDao()
					.findAll(PageRequest.of(page, size, Direction.DESC, "username", "spaceAndEmail")).getContent()
					.stream().map(this.userConverter::toBoundary).collect(Collectors.toList());
		}
		throw new UnAuthorizedError(
				"Admin space: " + adminSpace + ", Admin email: " + adminEmail + " is not authorized to get all users");
	}

	/**
	 * Delete all the users.
	 *
	 * @param adminSpace:
	 *            admin space.
	 * @param adminEmail:
	 *            admin email.
	 * 
	 * @exception UnAuthorizedError
	 *                in case the user is not an admin user.
	 */
	@Override
	public void deleteAllUsers(String adminSpace, String adminEmail) {
		if (this.userOperations.isUserRoleAsExcpected(adminSpace, adminEmail, UserRole.ADMIN))
			this.userOperations.getUserDao().deleteAll();
		else
			throw new UnAuthorizedError("Admin space: " + adminSpace + ", Admin email: " + adminEmail
					+ " is not authorized to delete all users");
	}

	/**
	 * Given a userRole, validates if it's Admin role.
	 *
	 * @param userBoundary:
	 *            user boundary object.
	 * 
	 * @exception: BadRequest:
	 *                 in case one of the userBoundary object attributes is not
	 *                 valid, empty or null.
	 */
	private void validateNewUserBoundaryParams(UserBoundary newUser) {

		String newUserAvatar = newUser.getAvatar();
		String newUserUsername = newUser.getUsername();
		String newUserEmail = newUser.getUserId().getEmail();

		UserBoundaryRole newUserRole = newUser.getRole();

		String[] userStringAttributes = { newUserAvatar, newUserUsername, newUserEmail };

		// String attributes checks
		for (String attribute : userStringAttributes) {
			if (this.utils.isObjectNull(attribute))
				throw new BadRequest("Null arguments are invalid!!");
			if (this.utils.isStringEmpty(attribute))
				throw new BadRequest("Empty arguments are invalid!!");
		}

		if (this.utils.isObjectNull(newUserRole))
			throw new BadRequest("Null arguments are invalid!!");
	}

}
