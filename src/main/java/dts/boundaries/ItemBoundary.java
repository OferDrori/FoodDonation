package dts.boundaries;

import java.util.Date;
import java.util.Map;

import dts.display.CreatedBy;
import dts.display.ItemId;
import dts.display.Location;

public class ItemBoundary{
    private ItemId itemId;
    private String type;
    private String name;
    private Boolean active;
    private Date createdTimestamp;
    private CreatedBy createdBy;
    private Location location;
    private Map<String, Object> itemAttributes;


    public ItemBoundary() {
    }

    /**
     * init digital item boundary
     *
     * @param itemId
     * @param type
     * @param name
     * @param active
     * @param createdTimeStamp
     * @param createdBy
     * @param location
     * @param itemAttributes
     */
    public ItemBoundary(ItemId itemId, String type, String name, Boolean active, Date createdTimeStamp, CreatedBy createdBy,
                        Location location, Map<String, Object> itemAttributes) {
        this.itemId = itemId;
        this.type = type;
        this.name = name;
        this.active = active;
        this.createdTimestamp = createdTimeStamp;
        this.createdBy = createdBy;
        this.location = location;
        this.itemAttributes = itemAttributes;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimeStamp) {
        this.createdTimestamp = createdTimeStamp;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<String, Object> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Map<String, Object> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    @Override
    public String toString() {
        return "ItemBoundary{" +
                "itemId=" + itemId +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", createdTimeStamp=" + createdTimestamp +
                ", createdBy=" + createdBy +
                ", location=" + location +
                ", itemAttributes=" + itemAttributes +
                '}';
    }
}
