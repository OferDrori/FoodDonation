package dts;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dts.boundaries.ItemBoundary;
import dts.boundaries.ItemIdBoundary;
import dts.data.ItemEntity;
import dts.display.Location;
import dts.display.UserRole;
import dts.errors.BadRequest;
import dts.errors.BaseErrorClass;
import dts.errors.UnAuthorizedError;
import dts.logic.GenerateDatabaseIds;
import dts.logic.ItemConverter;
import dts.logic.ItemsServicePagination;

@Service
public class RdbItemService implements ItemsServicePagination {

	private String managerSpace;
	private String itemSpace;
	private String userSpace;
	private String adminSpace;
	private ItemDaoOperations itemOperations;
	private UserDaoOperations userOperations;
	private ItemConverter itemConverter;
	private Utils utils;
	private GenerateDatabaseIds generateDatabaseIds;

	@Value("${spring.application.name}")
	public void setManagerSpace(String managerSpace) {
		this.managerSpace = managerSpace;
	}

	@Value("${spring.application.name}")
	public void setItemSpace(String itemSpace) {
		this.itemSpace = itemSpace;
	}

	@Value("${spring.application.name}")
	public void setUserSpace(String userSpace) {
		this.userSpace = userSpace;
	}

	@Value("${spring.application.name}")
	public void setAdminSpace(String adminSpace) {
		this.adminSpace = adminSpace;
	}

	@Autowired
	public RdbItemService(UserDaoOperations userOperations, ItemDaoOperations itemOperations,
			ItemConverter itemConverter, GenerateDatabaseIds generateDatabaseIds) {
		this.userOperations = userOperations;
		this.itemOperations = itemOperations;
		this.itemConverter = itemConverter;
		this.generateDatabaseIds = generateDatabaseIds;
	}

	@PostConstruct
	public void init() {
		this.utils = new Utils();
	}

	/**
	 * Creates Item and inserts it into the DB in case of a success.
	 *
	 * @param managerSpace:
	 *            manager space.
	 * @param managerEmail:
	 *            manager email.
	 * @param newItem:
	 *            a new item boundary from the client.
	 * 
	 * @return ItemBoundary: a new item boundary if was created successfully.
	 */
	@Override
	@Transactional
	public ItemBoundary create(String managerSpace, String managerEmail, ItemBoundary newItem) {

		if (this.userOperations.isUserRoleAsExcpected(managerSpace, managerEmail, UserRole.MANAGER)) {

			if (this.utils.areStringsEqual(this.managerSpace, managerSpace)) {

				if (newItem.getItemAttributes() == null)
					newItem.setItemAttributes(new HashMap<String, Object>());

				validateNewItemBoundaryParams(newItem);

				ItemEntity newItemEntity = this.itemConverter.toEntity(newItem);

				Long newId = this.generateDatabaseIds.getNextId();

				newItemEntity.setCreatedTimeStamp(new Date());
				newItemEntity.setSpaceItemId(this.itemSpace + ":" + Long.toString(newId));
				newItemEntity.setUserSpace(managerSpace);
				newItemEntity.setEmail(managerEmail);

				newItemEntity = this.itemOperations.getItemDao().save(newItemEntity);

				return this.itemConverter.toBoundary(newItemEntity);
			} else
				throw new BadRequest("manager space: " + managerSpace + " is invalid!!");

		} else
			throw new UnAuthorizedError("manager space: " + managerSpace + ", manager email: " + managerEmail
					+ " is not authorized to create items");

	}

	/**
	 * updates an item in case of a success.
	 *
	 * @param managerSpace:
	 *            manager space.
	 * @param managerEmail:
	 *            manager email.
	 * @param itemSpace:
	 *            item space.
	 * @param itemId:
	 *            item ID.
	 * @param update:
	 *            a new item boundary with updates
	 * 
	 * @exception BadRequest:
	 *                in case the managerSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager user.
	 * 
	 * @return ItemBoundary: an updated item boundary if was updated successfully.
	 */
	@Override
	public ItemBoundary update(String managerSpace, String managerEmail, String itemSpace, String itemId,
			ItemBoundary update) {

		if (this.userOperations.isUserRoleAsExcpected(managerSpace, managerEmail, UserRole.MANAGER)) {

			ItemEntity updatedItemEntity = this.itemOperations.getItemFromDB(itemSpace, itemId, false);

			if (this.utils.areStringsEqual(this.managerSpace, managerSpace)
					&& this.utils.areStringsEqual(this.itemSpace, itemSpace)) {

				if (update.getItemAttributes() == null || !update.getItemAttributes().isEmpty())
					update.setItemAttributes(new HashMap<String, Object>());

				ItemEntity updateBoundary = this.itemConverter.toEntity(update);

				if (!this.utils.isObjectNull(updateBoundary.getName()))
					updatedItemEntity.setName(updateBoundary.getName());

				if (!this.utils.isObjectNull(updateBoundary.getType()))
					updatedItemEntity.setType(updateBoundary.getType());

				if (!this.utils.isObjectNull(updateBoundary.getActive()))
					updatedItemEntity.setActive(updateBoundary.getActive());

				if (!this.utils.isObjectNull(updateBoundary.getLng()))
					updatedItemEntity.setLng(updateBoundary.getLng());

				if (!this.utils.isObjectNull(updateBoundary.getLat()))
					updatedItemEntity.setLat(updateBoundary.getLat());

				updatedItemEntity = this.itemOperations.getItemDao().save(updatedItemEntity);

				return this.itemConverter.toBoundary(updatedItemEntity);
			} else
				throw new BadRequest("Item space " + itemSpace + " is invalid!!!");
		} else
			throw new UnAuthorizedError("manager space: " + managerSpace + ", manager email: " + managerEmail
					+ " is not authorized to update items");
	}

	/**
	 * gets all the items from the DB using pagination.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param page:
	 *            how many pages to skip.
	 * @param size:
	 *            number of items to get from DB.
	 * 
	 * 
	 * @exception BadRequest:
	 *                in case the userSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager/player user.
	 * 
	 * @return List<ItemBoundary>: all requested items from the DB.
	 */
	@Override
	public List<ItemBoundary> getAllItemsByPagination(String userSpace, String userEmail, int page, int size) {

		if (this.utils.areStringsEqual(this.userSpace, userSpace)) {

			if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.MANAGER))
				return StreamSupport.stream(this.itemOperations.getAllItemsFromDB(page, size).spliterator(), false)
						.map(this.itemConverter::toBoundary).collect(Collectors.toList());
			else if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.PLAYER))
				return this.itemOperations.getAllItemsByActiveStateFromDB(true, page, size).stream()
						.map(this.itemConverter::toBoundary).collect(Collectors.toList());
			else
				throw new UnAuthorizedError(
						"user space: " + userSpace + " user email: " + userEmail + " cannot get all items");

		} else
			throw new BadRequest("User space " + userSpace + " is invalid!!!");
	}

	/**
	 * deprecated method!
	 */
	@Override
	public List<ItemBoundary> getAll(String userSpace, String userEmail) {
		/*
		 * This method is not used but must be in the spec due to Eyal request, do not
		 * delete it!!
		 */
		throw new BaseErrorClass("deprecated");
	}

	/**
	 * gets all the items from the DB by name convention.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param page:
	 *            how many pages to skip.
	 * @param size:
	 *            number of items to get from DB.
	 * @param namePattern
	 *            - the name pattern to search for.
	 * 
	 * 
	 * @exception BadRequest:
	 *                in case the userSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager/player user.
	 * 
	 * @return List<ItemBoundary>: all requested items from the DB.
	 */
	@Override
	public List<ItemBoundary> getAllItemsByNamePattern(String userSpace, String userEmail, int page, int size,
			String namePattern) {

		if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.PLAYER))
			return this.itemOperations.getAllItemsByNamePatternFromDB(true, namePattern, page, size).stream()
					.map(this.itemConverter::toBoundary).collect(Collectors.toList());
		else if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.MANAGER))
			return this.itemOperations.getAllItemsByNamePatternFromDB(namePattern, page, size).stream()
					.map(this.itemConverter::toBoundary).collect(Collectors.toList());
		else
			throw new UnAuthorizedError(
					"user space: " + userSpace + " user email: " + userEmail + " cannot get an item");
	}

	/**
	 * gets all the items from the DB by name convention.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param page:
	 *            how many pages to skip.
	 * @param size:
	 *            number of items to get from DB.
	 * @param type
	 *            - the type pattern to search for.
	 * 
	 * 
	 * @exception BadRequest:
	 *                in case the userSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager/player user.
	 * 
	 * @return List<ItemBoundary>: all requested items from the DB.
	 */
	@Override
	public List<ItemBoundary> getAllItemsByType(String userSpace, String userEmail, int page, int size, String type) {

		if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.PLAYER))
			return this.itemOperations.getAllItemsByTypeFromDB(true, type, page, size).stream()
					.map(this.itemConverter::toBoundary).collect(Collectors.toList());
		else if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.MANAGER))
			return this.itemOperations.getAllItemsByTypeFromDB(type, page, size).stream()
					.map(this.itemConverter::toBoundary).collect(Collectors.toList());
		else
			throw new UnAuthorizedError(
					"user space: " + userSpace + " user email: " + userEmail + " cannot get an item");
	}

	/**
	 * gets all the items from the DB by location.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param page:
	 *            how many pages to skip.
	 * @param size:
	 *            number of items to get from DB.
	 * @param lat:
	 *            latitude
	 * @param lng:
	 *            longtitude
	 * @param distance:
	 *            distance of the area the search in.
	 * 
	 * @exception BadRequest:
	 *                in case the userSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager/player user.
	 * 
	 * @return List<ItemBoundary>: all requested items from the DB.
	 */
	@Override
	public List<ItemBoundary> getAllItemsByLocation(String userSpace, String userEmail, int page, int size, double lat,
			double lng, double distance) {
		if (distance < 0) {
			throw new BadRequest("distance cannot be negative");
		}
		if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.PLAYER))
			return this.itemOperations.getAllItemsByLocationFromDB(true, page, size, lat, lng, distance).stream()
					.map(this.itemConverter::toBoundary).collect(Collectors.toList());
		else if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.MANAGER))
			return this.itemOperations.getAllItemsByLocationFromDB(page, size, lat, lng, distance).stream()
					.map(this.itemConverter::toBoundary).collect(Collectors.toList());
		else
			throw new UnAuthorizedError(
					"user space: " + userSpace + " user email: " + userEmail + " cannot get an item");
	}

	/**
	 * gets a single item from the DB
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param itemSpace:
	 *            item space.
	 * @param itemId:
	 *            item ID.
	 * 
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager/player user.
	 * 
	 * @return ItemBoundary: item boundary in case it was found in the DB.
	 */
	@Override
	public ItemBoundary getSpecificItem(String userSpace, String userEmail, String itemSpace, String itemId) {

		if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.PLAYER))
			return this.itemConverter.toBoundary(this.itemOperations.getItemFromDB(itemSpace, itemId, true));
		else if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.MANAGER))
			return this.itemConverter.toBoundary(this.itemOperations.getItemFromDB(itemSpace, itemId, false));
		else
			throw new UnAuthorizedError(
					"user space: " + userSpace + " user email: " + userEmail + " cannot get an item");
	}

	/**
	 * deletes all the items in the DB.
	 *
	 * @param adminSpace:
	 *            admin space.
	 * @param adminEmail:
	 *            admin email.
	 * 
	 * @exception UnAuthorizedError:
	 *                in case the user is not admin user.
	 */
	@Override
	public void deleteAll(String adminSpace, String adminEmail) {

		if (this.utils.areStringsEqual(this.adminSpace, adminSpace))
			if (this.userOperations.isUserRoleAsExcpected(adminSpace, adminEmail, UserRole.ADMIN))
				this.itemOperations.getItemDao().deleteAll();
			else
				throw new UnAuthorizedError(
						"admin space: " + adminSpace + " admin email: " + adminEmail + " cannot delete all items");
		else
			throw new BadRequest("admin space " + adminSpace + " is invalid!!!");
	}

	/**
	 * binds an existing item to another item.
	 *
	 * @param managerSpace:
	 *            manager space.
	 * @param managerEmail:
	 *            manager email.
	 * @param itemSpace:
	 *            item space.
	 * @param originalItemId:
	 *            original item ID.
	 * @param itemIdBoundary:
	 *            a new item ID boundary to bind to.
	 * 
	 * @exception BadRequest:
	 *                in case the managerSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager user.
	 * 
	 */
	@Override
	@Transactional
	public void bindExistingItemToExistingChild(String managerSpace, String managerEmail, String itemSpace,
			String originalItemId, ItemIdBoundary itemIdBoundary) {

		if (this.userOperations.isUserRoleAsExcpected(managerSpace, managerEmail, UserRole.MANAGER)) {
			ItemEntity originalEntity = this.itemOperations.getItemFromDB(itemSpace, originalItemId, false);

			ItemEntity childEntity = this.itemOperations.getItemFromDB(itemSpace, itemIdBoundary.getId(), false);

			if (this.utils.areStringsEqual(this.managerSpace, managerSpace)
					&& this.utils.areStringsEqual(this.itemSpace, itemSpace)) {

				childEntity.setParent(originalEntity);

				originalEntity.addChild(childEntity);

				this.itemOperations.getItemDao().save(originalEntity);
			} else
				throw new BadRequest(
						"One of manager space: " + managerSpace + " item space " + itemSpace + " is invalid");
		} else
			throw new UnAuthorizedError("manager space: " + managerSpace + " manager email: " + managerEmail
					+ " cannot bind existing item to existing child");
	}

	/**
	 * gets all the children items from the DB of a specific parent.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param page:
	 *            how many pages to skip.
	 * @param size:
	 *            number of items to get from DB.
	 * @param parentSpace:
	 *            parent space.
	 * @param parentId
	 *            item parent ID.
	 * 
	 * 
	 * @exception BadRequest:
	 *                in case the userSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager/player user.
	 * 
	 * @return List<ItemBoundary>: all requested items from the DB.
	 */
	@Override
	public List<ItemBoundary> getAllChildrenFromItem(String userSpace, String userEmail, String parentSpace,
			String parentId, int page, int size) {

		if (this.utils.areStringsEqual(this.userSpace, userSpace)
				&& this.utils.areStringsEqual(this.itemSpace, parentSpace)) {

			if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.MANAGER))
				return this.itemOperations.getAllChildrenItemsByParentIdFromDB(page, size, parentSpace, parentId)
						.stream().map(this.itemConverter::toBoundary).collect(Collectors.toList());
			else if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.PLAYER))
				return this.itemOperations.getAllChildrenItemsByParentIdFromDB(true, page, size, parentSpace, parentId)
						.stream().map(this.itemConverter::toBoundary).collect(Collectors.toList());
			else
				throw new UnAuthorizedError("user space: " + userSpace + " user email: " + userEmail
						+ " cannot get all children of an item");
		} else
			throw new BadRequest("One of user space: " + userSpace + " item space " + parentSpace + " is invalid");
	}

	/**
	 * gets all the parent(s) items from the DB of a specific child.
	 *
	 * @param userSpace:
	 *            user space.
	 * @param userEmail:
	 *            user email.
	 * @param page:
	 *            how many pages to skip.
	 * @param size:
	 *            number of items to get from DB.
	 * @param childSpace:
	 *            child space.
	 * @param childId
	 *            item child ID.
	 * 
	 * 
	 * @exception BadRequest:
	 *                in case the userSpace is not valid.
	 * @exception UnAuthorizedError:
	 *                in case the user is not a manager/player user.
	 * 
	 * @return List<ItemBoundary>: all requested items from the DB.
	 */
	@Override
	public List<ItemBoundary> getAllParentOfItem(String userSpace, String userEmail, String childSpace, String childId,
			int page, int size) {

		if (this.utils.areStringsEqual(this.userSpace, userSpace)
				&& this.utils.areStringsEqual(this.itemSpace, childSpace)) {

			if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.MANAGER))
				return this.itemOperations.getAllParentsItemsByChildIdFromDB(page, size, childSpace, childId).stream()
						.map(this.itemConverter::toBoundary).collect(Collectors.toList());
			else if (this.userOperations.isUserRoleAsExcpected(userSpace, userEmail, UserRole.PLAYER))
				return this.itemOperations.getAllParentsItemsByChildIdFromDB(true, page, size, childSpace, childId)
						.stream().map(this.itemConverter::toBoundary).collect(Collectors.toList());
			else
				throw new UnAuthorizedError("user space: " + userSpace + " user email: " + userEmail
						+ " cannot get all parents of an item");
		} else
			throw new BadRequest("One of user space: " + userSpace + " item space " + childSpace + " is invalid");
	}

	/**
	 * validates input data of an item from the user
	 *
	 * @param newItem:
	 *            a new item that the client requested.
	 * 
	 * @exception BadRequest:
	 *                in case a bad input from the client.
	 */
	private void validateNewItemBoundaryParams(ItemBoundary newItem) {

		String type = newItem.getType();
		String name = newItem.getName();
		Boolean active = newItem.getActive();
		Location location = newItem.getLocation();

		Object[] allItemsAttributes = { type, name, active, location };

		for (Object attr : allItemsAttributes) {
			if (this.utils.isObjectNull(attr))
				throw new BadRequest("Null arguments are invalid!!");
			if (attr instanceof String && this.utils.isStringEmpty((String) attr))
				throw new BadRequest("Empty arguments are invalid!!");
		}

		if (!this.utils.areStringsEqual(type, "DONATOR"))
			throw new BadRequest("type " + type + " is invalid! must be DONATOR");

		Map<String, Object> itemAttributes = newItem.getItemAttributes();
		if (!itemAttributes.isEmpty())
			throw new BadRequest("Item attriubutes must be empty when creating an item!");

	}
}
