package dts.logic;

import dts.boundaries.ItemBoundary;

import java.util.List;

/**
 * A item service interface that defines all the items api functionality.
 */
public interface ItemsService {
	public ItemBoundary create(String managerSpace, String managerEmail, ItemBoundary newItem);

	public ItemBoundary update(String managerSpace, String managerEmail, String itemSpace, String itemId,
			ItemBoundary update);

	public List<ItemBoundary> getAll(String userSpace, String userEmail);

	public ItemBoundary getSpecificItem(String userSpace, String userEmail, String itemSpace, String itemId);

	public void deleteAll(String adminSpace, String adminEmail);
}