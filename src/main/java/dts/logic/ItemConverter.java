package dts.logic;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dts.Jackson;
import dts.Utils;
import dts.boundaries.ItemBoundary;
import dts.data.ItemEntity;
import dts.display.CreatedBy;
import dts.display.ItemId;
import dts.display.Location;
import dts.display.UserId;

@Component
public class ItemConverter {

	private Jackson jackson;
	private Utils utils;

	@Autowired
	public ItemConverter(Jackson jackson) {
		this.jackson = jackson;
	}

	@PostConstruct
	public void init() {
		this.utils = new Utils();
	}

	/**
	 * Converts from ItemBoundary object to a ItemEntity object.
	 *
	 * @param itemBoundary:
	 *            a ItemBoundary object.
	 * @return ItemEntity object that matches the ItemBoundary object.
	 */
	public ItemEntity toEntity(ItemBoundary itemBoundary) {

		ItemEntity itemEntity = new ItemEntity();

		// set item type
		if (!this.utils.isObjectNull(itemBoundary.getType()))
			itemEntity.setType(itemBoundary.getType());
		// set name
		if (!this.utils.isObjectNull(itemBoundary.getName()))
			itemEntity.setName(itemBoundary.getName());
		// set active boolean
		if (!this.utils.isObjectNull(itemBoundary.getActive()))
			itemEntity.setActive(itemBoundary.getActive());
		// set created time stamp (Date)
		if (!this.utils.isObjectNull(itemBoundary.getCreatedTimestamp()))
			itemEntity.setCreatedTimeStamp(itemBoundary.getCreatedTimestamp());
		// set location as lng and lat
		if (!this.utils.isObjectNull(itemBoundary.getLocation())) {
			itemEntity.setLat(itemBoundary.getLocation().getLat());
			itemEntity.setLng(itemBoundary.getLocation().getLng());
		}
		// set item attributes
		itemEntity.setItemAttributes(this.jackson.fromObjectToStringUsingJackson(
				this.jackson.fromMapToReservationsObject(itemBoundary.getItemAttributes())));

		return itemEntity;

	}

	/**
	 * i Converts from itemEntity object to a itemBoundary object.
	 *
	 * @param itemEntity:
	 *            a itemEntity object.
	 * @return itemBoundary object that matches the itemEntity object.
	 */
	public ItemBoundary toBoundary(ItemEntity itemEntity) {
		ItemBoundary itemBoundary = new ItemBoundary();

		// set item ID object
		itemBoundary.setItemId(this.fromSpaceAndItemIdToItem(itemEntity.getSpaceItemId()));
		// set type
		itemBoundary.setType(itemEntity.getType());
		// set name
		itemBoundary.setName(itemEntity.getName());
		// set active boolean
		itemBoundary.setActive(itemEntity.getActive());
		// set date
		itemBoundary.setCreatedTimestamp(itemEntity.getCreatedTimeStamp());
		// set createBy object
		itemBoundary.setCreatedBy(new CreatedBy(new UserId(itemEntity.getUserSpace(), itemEntity.getEmail())));
		// set location object.
		itemBoundary.setLocation(new Location(itemEntity.getLat(), itemEntity.getLng()));
		// set item attributes map.
		itemBoundary.setItemAttributes(this.jackson.fromStringToMapUsingJackson(itemEntity.getItemAttributes()));

		return itemBoundary;
	}

	private ItemId fromSpaceAndItemIdToItem(String spaceAndItemId) {
		if (spaceAndItemId != null) {
			String[] args = spaceAndItemId.split(":");
			return new ItemId(args[0], args[1]);
		} else {
			return null;
		}
	}
}
