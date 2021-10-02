package dts.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dts.Utils;
import dts.boundaries.OperationBoundary;
import dts.boundaries.UserBoundary;
import dts.logic.ItemsService;
import dts.logic.OperationServicePagination;
import dts.logic.UserServicePagination;

@RestController
public class AdminController {

	private UserServicePagination userHandler;
	private ItemsService itemHandler;
	private OperationServicePagination operationHandler;
	private Utils utils;

	@Autowired
	public void setUserHandler(UserServicePagination userServiceImplementation) {
		this.userHandler = userServiceImplementation;
	}

	@Autowired
	public void setItemHandler(ItemsService itemServiceImplementation) {
		this.itemHandler = itemServiceImplementation;
	}

	@Autowired
	public void setOperationHandler(OperationServicePagination operationServiceImplementation) {
		this.operationHandler = operationServiceImplementation;
	}

	@PostConstruct
	public void initUtils() {
		this.utils = new Utils();
	}

	/**
	 * Implements the 'DELETE' Http method for all users deletion. Deletes all the
	 * users in the database, Will only apply if the user has "ADMIN" role. This
	 * method should return status code 204 in case of success.
	 *
	 * @param adminSpace:
	 *            admin space from URL.
	 * @param adminEmail:
	 *            admin email from URL.
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/dts/admin/users/{adminSpace}/{adminEmail}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllUsers(@PathVariable("adminSpace") String adminSpace,
			@PathVariable("adminEmail") String adminEmail) {
		this.userHandler.deleteAllUsers(adminSpace, adminEmail);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/dts/admin/items/{adminSpace}/{adminEmail}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllItems(@PathVariable("adminSpace") String adminSpace,
			@PathVariable("adminEmail") String adminEmail) throws Exception {
		this.itemHandler.deleteAll(adminSpace, adminEmail);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/dts/admin/operations/{adminSpace}/{adminEmail}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllOperations(@PathVariable("adminSpace") String adminSpace,
			@PathVariable("adminEmail") String adminEmail) {
		this.operationHandler.deleteAllOperations(adminSpace, adminEmail);
	}

	/**
	 * Implements the 'GET' Http method to get all users from the DB. Gets all the
	 * users in the database, Will only apply if the user has "ADMIN" role. This
	 * method should return status code 200 in case of success.
	 *
	 * @param adminSpace:
	 *            admin space from URL.
	 * @param adminEmail:
	 *            admin email from URL.
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/dts/admin/users/{adminSpace}/{adminEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] getAllUsers(
			@PathVariable("adminSpace") String adminSpace,
			@PathVariable("adminEmail") String adminEmail, 	
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.utils.fromUserBoundaryListToArray(this.userHandler.getAllUsersWithPagination(adminSpace, adminEmail, size, page));
	}

	@RequestMapping(path = "/dts/admin/operations/{adminSpace}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public OperationBoundary[] getAllOperations(
			@PathVariable("adminSpace") String adminSpace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		
		return this.utils.fromOperationBoundaryListToArray(
				this.operationHandler.getAllOperationsWithPagination(adminSpace, adminEmail, size, page));

	}
}
