package dts.logic;

import java.util.List;

import dts.boundaries.ItemBoundary;
import dts.boundaries.ItemIdBoundary;

public interface ItemsServicePagination extends ItemsService {

	public List<ItemBoundary> getAllChildrenFromItem(String userSpace, String userEmail, String parentSpace,
			String parentId, int page, int size);

	public List<ItemBoundary> getAllParentOfItem(String userSpace, String userEmail, String childSpace, String childId,
			int page, int size);

	public void bindExistingItemToExistingChild(String managerSpace, String managerEmail, String itemSpace,
			String originalItemId, ItemIdBoundary itemIdBoundary);

	public List<ItemBoundary> getAllItemsByPagination(String userSpace, String userEmail, int page, int size);

	public List<ItemBoundary> getAllItemsByNamePattern(String userSpace, String userEmail, int page, int size,
			String namePattern);

	public List<ItemBoundary> getAllItemsByType(String userSpace, String userEmail, int page, int size, String type);

	public List<ItemBoundary> getAllItemsByLocation(String userSpace, String userEmail, int page, int size, double lat,
			double lng, double distance);
}
