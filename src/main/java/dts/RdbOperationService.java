package dts;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dts.boundaries.OperationBoundary;
import dts.data.ItemEntity;
import dts.data.OperationEntity;
import dts.display.InvokedBy;
import dts.display.Item;
import dts.display.ItemId;
import dts.display.UserId;
import dts.display.UserRole;
import dts.errors.BadRequest;
import dts.errors.BaseErrorClass;
import dts.errors.UnAuthorizedError;
import dts.logic.FoodDonationOperations;
import dts.logic.GenerateDatabaseIds;
import dts.logic.IdGeneratorEntityDao;
import dts.logic.OperationConverter;
import dts.logic.OperationDao;
import dts.logic.OperationServicePagination;

@Service
public class RdbOperationService implements OperationServicePagination, Constants {

	private OperationConverter operationConvertor;
	private OperationDao operationDao;
	private UserDaoOperations userOperations;
	private ItemDaoOperations itemOperations;
	private String operationSpace;
	private String adminSpace;
	private Utils utils;
	private ConfigurableApplicationContext appContext;
	private GenerateDatabaseIds generateDatabaseIds;

	@Value("${spring.application.name}")
	public void setOperationSpace(String operationSpace) {
		this.operationSpace = operationSpace;
	}

	@Value("${spring.application.name}")
	public void setAdminSpace(String adminSpace) {
		this.adminSpace = adminSpace;
	}

	@Autowired
	public RdbOperationService(OperationConverter operationConvertor, OperationDao operationDao,
			IdGeneratorEntityDao idGeneratorEntityDao, UserDaoOperations userOperations,
			ItemDaoOperations itemOperations, ConfigurableApplicationContext appContext,
			GenerateDatabaseIds generateDatabaseIds) {
		this.operationConvertor = operationConvertor;
		this.operationDao = operationDao;
		this.userOperations = userOperations;
		this.itemOperations = itemOperations;
		this.appContext = appContext;
		this.generateDatabaseIds = generateDatabaseIds;
	}

	@PostConstruct
	public void init() {
		this.utils = new Utils();
	}

	/**
	 * Creates operation object and inserts it into the DB in case the operation was successful.
	 *
	 * @param operation:
	 *            the object from the client.
	 * 
	 * @exception BadRequest: in case the user space is not valid.
	 * @exception UnAuthorizedError: in case the user is not a player user.
	 * 
	 * @return operation boundary object if created and inserted into DB
	 *         successfully.
	 */
	@Override
	@Transactional
	public Object invokeOperation(OperationBoundary operation) {

		validateNewOperationBoundaryParams(operation);

		String userSpace = operation.getInvokedBy().getUserId().getSpace();
		String email = operation.getInvokedBy().getUserId().getEmail();

		String itemSpace = operation.getItem().getItemId().getSpace();
		String itemId = operation.getItem().getItemId().getId();

		if (this.userOperations.isUserRoleAsExcpected(userSpace, email, UserRole.PLAYER)) {

			ItemEntity itemEntity = this.itemOperations.getItemFromDB(itemSpace, itemId, false);
			if (itemEntity.getActive() == true) {

				this.itemOperations.getItemDao().save(commitOperation(operation, itemEntity));

				OperationEntity operationEntity = this.operationConvertor.toEntity(operation);

				Long newId = this.generateDatabaseIds.getNextId();

				operationEntity.setCreatedTimeStamp(new Date());
				operationEntity.setSpaceAndOperationId(this.operationSpace + ":" + Long.toString(newId));

				this.operationDao.save(operationEntity);

				return this.operationConvertor.toBoundary(operationEntity);
			} else
				throw new BadRequest("item space: " + itemSpace + " item ID: " + itemId
						+ " is not active! cannot perform the operation: " + operation.getType());

		} else
			throw new UnAuthorizedError(
					"user space: " + userSpace + " user email: " + email + " cannot invoke the operation");

	}

	private ItemEntity commitOperation(OperationBoundary operation, ItemEntity itemEntity) {
		/**
		 * This method executes an operation and initialize the bean classes according
		 * to the operatio action. Parses mainly the item attributes parameter.
		 */

		String type = operation.getType();

		if (this.utils.areStringsEqual(type, CREATE_PICKUP_WINDOW)
				|| this.utils.areStringsEqual(type, CANCEL_PICKUP_WINDOW)
				|| this.utils.areStringsEqual(type, UPDATE_PICKUP_WINDOW)
				|| this.utils.areStringsEqual(type, ADD_NEW_DONATOR_FOOD)
				|| this.utils.areStringsEqual(type, REMOVE_DONATOR_FOOD))
			return this.appContext.getBean(DONATOR, FoodDonationOperations.class).invokeOperation(operation,
					itemEntity);
		else
			return this.appContext.getBean(RECIEVER, FoodDonationOperations.class).invokeOperation(operation,
					itemEntity);
	}

	/**
	 * deprecated method!
	 */
	@Override
	public List<OperationBoundary> getAllOperations(String adminSpace, String adminEmail) {

		/*
		 * This method is not used but must be in the spec due to Eyal request, do not
		 * delete it!!
		 */
		throw new BaseErrorClass("deprecated");
	}

	/**
	 * Delete all the operations from the DB.
	 *
	 * @param adminSpace:
	 *            admin space.
	 * @param adminEmail:
	 *            admin email.
	 * 
	 * @exception UnAuthorized
	 *                in case the user is not an admin user.
	 */
	@Override
	public void deleteAllOperations(String adminSpace, String adminEmail) {

		if (this.utils.areStringsEqual(this.adminSpace, adminSpace))
			if (this.userOperations.isUserRoleAsExcpected(adminSpace, adminEmail, UserRole.ADMIN))
				this.operationDao.deleteAll();
			else
				throw new UnAuthorizedError("user space: " + adminSpace + " user email: " + adminEmail
						+ " is not authorized to delete all operations");
		else
			throw new BadRequest("admin space " + adminSpace + " is invalid!!");
	}

	/**
	 * given an operation boundary object, validates that all the fields and
	 * subfields are not empty or null.
	 *
	 * @param operationBoundary:
	 *            the operation boundary object to validate.
	 * 
	 * @exception BadRequest
	 *                in case one of the fields in operation boundary from the
	 *                client is null or empty.
	 *
	 */
	private void validateNewOperationBoundaryParams(OperationBoundary operationBoundary) {

		// attributes of operationBoundary
		Item item = operationBoundary.getItem();
		InvokedBy invokedBy = operationBoundary.getInvokedBy();

		Object[] allOperationAttributes = { item, invokedBy };

		for (Object attr : allOperationAttributes) {
			if (this.utils.isObjectNull(attr))
				throw new BadRequest("Null arguments are invalid!!");
		}
		// sub attributes of operationBoundary
		ItemId itemIdObject = item.getItemId();
		UserId userId = invokedBy.getUserId();

		String type = operationBoundary.getType();
		String itemId = itemIdObject.getId();
		String itemSpace = itemIdObject.getSpace();
		String email = userId.getEmail();
		String userSpace = userId.getSpace();

		Object[] allSubOperationAttributes = { type, userSpace, itemIdObject, itemSpace, userId, itemId, email };

		for (Object attr : allSubOperationAttributes) {
			if (this.utils.isObjectNull(attr))
				throw new BadRequest("Null arguments are invalid!!");
			if (attr instanceof String && this.utils.isStringEmpty((String) attr))
				throw new BadRequest("Empty arguments are invalid!!");
		}

		if (!this.utils.areStringsEqual(this.adminSpace, userSpace))
			throw new BadRequest("user space: " + userSpace + " is invalid!!!");

	}

	/**
	 * Gets all operations from the DB using pagination
	 *
	 * @param adminSpace:
	 *            admin space.
	 * @param adminEmail:
	 *            admin email
	 * @param size:
	 *            how many operation to get from the DB.
	 * @param page:
	 *            how many pages to skip in order to get each operation from the DB.
	 * 
	 * @exception BadRequest:
	 *                in case the admin space is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not an admin user.
	 * 
	 *
	 */
	@Override
	@Transactional(readOnly = true)
	public List<OperationBoundary> getAllOperationsWithPagination(String adminSpace, String adminEmail, int size,
			int page) {

		if (this.utils.areStringsEqual(this.adminSpace, adminSpace)) {

			if (this.userOperations.isUserRoleAsExcpected(adminSpace, adminEmail, UserRole.ADMIN)) {
				return this.operationDao
						.findAll(PageRequest.of(page, size, Direction.DESC, "createdTimeStamp", "spaceAndOperationId"))
						.getContent().stream().map(this.operationConvertor::toBoundary).collect(Collectors.toList());
			} else
				throw new UnAuthorizedError("user space: " + adminSpace + " user email: " + adminEmail
						+ " is not authorized to get all operations");

		} else
			throw new BadRequest("admin space " + adminSpace + " is invalid!!");

	}
}
