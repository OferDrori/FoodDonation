package dts;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import dts.data.ItemEntity;
import dts.errors.ItemNotFound;
import dts.logic.ItemDao;

@Component
public class ItemDaoOperations {

	private ItemDao itemDao;

	@Autowired
	public ItemDaoOperations(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public ItemEntity getItemFromDB(String itemSpace, String itemId, boolean isActiveItemRequired) {
		Optional<ItemEntity> itemEntity = this.itemDao.findById(itemSpace + ":" + itemId);
		if (itemEntity.isPresent())

			if (isActiveItemRequired == true)
				if (itemEntity.get().getActive() == true)
					return itemEntity.get();
				else
					throw new ItemNotFound("item space: " + itemSpace + " item ID: " + itemId + " was not found!!");
			else
				return itemEntity.get();
		else
			throw new ItemNotFound("item space: " + itemSpace + " item ID: " + itemId + " was not found!!");
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllItemsByActiveStateFromDB(boolean areActiveItemsRequired, int page, int size) {

		return this.itemDao.findAllItemsByActive(areActiveItemsRequired,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public Iterable<ItemEntity> getAllItemsFromDB(int page, int size) {

		return this.itemDao.findAll(PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllItemsByNamePatternFromDB(boolean areActiveItemsRequired, String pattern, int page,
			int size) {

		return this.itemDao.findAllItemsByActiveAndNameLike(areActiveItemsRequired, pattern,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllItemsByNamePatternFromDB(String pattern, int page, int size) {

		return this.itemDao.findAllItemsByNameLike(pattern,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllItemsByTypeFromDB(String type, int page, int size) {

		return this.itemDao.findAllItemsByTypeLike(type,
				PageRequest.of(page, size, Direction.ASC, "type", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllItemsByTypeFromDB(boolean areActiveItemsRequired, String type, int page, int size) {

		return this.itemDao.findAllItemsByActiveAndTypeLike(areActiveItemsRequired, type,
				PageRequest.of(page, size, Direction.ASC, "type", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllItemsByLocationFromDB(boolean areActiveItemsRequired, int page, int size, double lat,
			double lng, double distance) {

		return this.itemDao
				.findAllByActiveAndLatGreaterThanEqualAndLatLessThanEqualAndLngGreaterThanEqualAndLngLessThanEqual(
						areActiveItemsRequired, lat - distance, lat + distance, lng - distance, lng + distance,
						PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllItemsByLocationFromDB(int page, int size, double lat, double lng, double distance) {

		return this.itemDao.findAllByLatGreaterThanEqualAndLatLessThanEqualAndLngGreaterThanEqualAndLngLessThanEqual(
				lat - distance, lat + distance, lng - distance, lng + distance,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllChildrenItemsByParentIdFromDB(int page, int size, String parentSpace,
			String parentId) {
		return this.itemDao.findAllByParent_spaceItemId(parentSpace + ":" + parentId,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllChildrenItemsByParentIdFromDB(boolean areActiveItemsRequired, int page, int size,
			String parentSpace, String parentId) {

		return this.itemDao.findAllByActiveAndParent_spaceItemId(areActiveItemsRequired, parentSpace + ":" + parentId,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));

	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllParentsItemsByChildIdFromDB(boolean areActiveItemsRequired, int page, int size,
			String childSpace, String childId) {

		return this.itemDao.findAllByActiveAndChildren_spaceItemId(areActiveItemsRequired, childSpace + ":" + childId,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));
	}

	@Transactional(readOnly = true)
	public List<ItemEntity> getAllParentsItemsByChildIdFromDB(int page, int size, String childSpace, String childId) {

		return this.itemDao.findAllByChildren_spaceItemId(childSpace + ":" + childId,
				PageRequest.of(page, size, Direction.ASC, "name", "spaceItemId"));

	}

	public ItemDao getItemDao() {
		return itemDao;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

}
