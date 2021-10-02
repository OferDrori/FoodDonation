package dts.api;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dts.Utils;
import dts.boundaries.ItemBoundary;
import dts.boundaries.ItemIdBoundary;
import dts.logic.ItemsServicePagination;

@RestController
public class DigitalItemController {

	private ItemsServicePagination itemHandler;
	private Utils utils;

	@Autowired
	public void setItemHandler(ItemsServicePagination itemServiceImplementation) {
		this.itemHandler = itemServiceImplementation;
	}

	@PostConstruct
	public void initUtils() {
		this.utils = new Utils();
	}

	/**
	 * This method implements the 'POST' Http method to create a new Item in the
	 * server.
	 *
	 * @param itemBoundary
	 *            Json object of item
	 * @return new itemBoundary Object
	 */
	@RequestMapping(path = "/dts/items/{managerSpace}/{managerEmail}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)

	public ItemBoundary createNewItem(@RequestBody ItemBoundary itemBoundary,
			@PathVariable("managerSpace") String space, @PathVariable("managerEmail") String email) {
		return itemHandler.create(space, email, itemBoundary);
	}

	/**
	 * This method implements the 'PUT' Http method to update a new Item in the
	 * server.
	 *
	 * @param updateItem
	 *            Json object of item
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/dts/items/{managerSpace}/{managerEmail}/{itemSpace}/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateItem(@PathVariable("managerSpace") String space, @PathVariable("managerEmail") String email,
			@PathVariable("itemSpace") String itemSpace, @PathVariable("itemId") String itemID,
			@RequestBody ItemBoundary updateItem) throws Exception {
		this.itemHandler.update(space, email, itemSpace, itemID, updateItem);
	}

	/**
	 * This method implements the 'GET' Http method to get a Item from the server.
	 *
	 * @param space
	 * @param email
	 * @param itemSpace
	 * @param itemId
	 * @return the item by id from the URL
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/dts/items/{userSpace}/{userEmail}/{itemSpace}/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary RetrieveSpecificItem(@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email, @PathVariable("itemSpace") String itemSpace,
			@PathVariable("itemId") String itemId) throws Exception {
		return this.itemHandler.getSpecificItem(space, email, itemSpace, itemId);
	}

	/**
	 * This method implements the 'GET' Http method to get all Items from the
	 * server.
	 *
	 * @param space
	 * @param email
	 * @return a array with all the items in the system
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/dts/items/{userSpace}/{userEmail}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllItems(@PathVariable("userSpace") String space, @PathVariable("userEmail") String email,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws Exception {
		return this.utils
				.fromItemBoundaryListToArray(this.itemHandler.getAllItemsByPagination(space, email, page, size));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/dts/items/{userSpace}/{userEmail}/search/byNamePattern/{namePattern}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllItemsByNamePattern(@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email, @PathVariable("namePattern") String namePattern,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws Exception {
		return this.utils.fromItemBoundaryListToArray(
				this.itemHandler.getAllItemsByNamePattern(space, email, page, size, namePattern));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/dts/items/{userSpace}/{userEmail}/search/byType/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllItemsByType(@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email, @PathVariable("type") String type,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws Exception {
		return this.utils
				.fromItemBoundaryListToArray(this.itemHandler.getAllItemsByType(space, email, page, size, type));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/dts/items/{userSpace}/{userEmail}/search/near/{lat}/{lng}/{distance}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllItemsByLocation(@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email, @PathVariable("lat") double lat, @PathVariable("lng") double lng,
			@PathVariable("distance") double distance,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws Exception {
		return this.utils.fromItemBoundaryListToArray(
				this.itemHandler.getAllItemsByLocation(space, email, page, size, lat, lng, distance));
	}

	/**
	 * This method implements 'PUT' http method to bind an existing item to an
	 * existing child item
	 * 
	 * @param space
	 * @param email
	 * @return a array with all the items in the system
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/dts/items/{managerSpace}/{managerEmail}/{itemSpace}/{itemId}/children", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void bindExistingItemToExistingChild(@PathVariable("managerSpace") String space,
			@PathVariable("managerEmail") String email, @PathVariable("itemSpace") String itemSpace,
			@PathVariable("itemId") String itemId, @RequestBody ItemIdBoundary itemIdBoundary) throws Exception {
		this.itemHandler.bindExistingItemToExistingChild(space, email, itemSpace, itemId, itemIdBoundary);
	}

	/**
	 * This method implements the 'GET' Http method to get all children of an
	 * existing item.
	 *
	 * @param space
	 * @param email
	 * @param itemSpace
	 * @param itemId
	 * @return the item by id from the URL
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/dts/items/{userSpace}/{userEmail}/{parentSpace}/{parentId}/children", produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllChildrenFromItem(@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email, @PathVariable("parentSpace") String parentSpace,
			@PathVariable("parentId") String parentId,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws Exception {
		return this.utils.fromItemBoundaryListToArray(
				this.itemHandler.getAllChildrenFromItem(space, email, parentSpace, parentId, page, size));
	}

	/**
	 * This method implements the 'GET' Http method to get an array with item
	 * parent.
	 *
	 * @param space
	 * @param email
	 * @param itemSpace
	 * @param itemId
	 * @return the item by id from the URL
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/dts/items/{userSpace}/{userEmail}/{childSpace}/{childId}/parents", produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllParentsOfItem(@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email, @PathVariable("childSpace") String childSpace,
			@PathVariable("childId") String childId,
			@RequestParam(name = "size", required = false, defaultValue = "15") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) throws Exception {
		return this.utils
				.fromItemBoundaryListToArray(this.itemHandler.getAllParentOfItem(space, email, childSpace, childId, page, size));
	}
}
