package dts.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ITEMS")
public class ItemEntity {

    private String type;
    private String name;
    private boolean active;
    private Date createdTimeStamp;
    private String email;
    private double lat;
    private double lng;
    private String itemAttributes;
    private String spaceItemId;
    private String userSpace;
    private ItemEntity parent;
    private Set<ItemEntity> children;
    // TODO: add ItemEntity's ItemAttributes for the ItemConverter toEntity method (to Boundary as well)


    public ItemEntity() {
    	this.children = new HashSet<>();
    }

    public ItemEntity(String type, String name, boolean active, Date createdTimeStamp, String email,
    		double lat, double lng, String itemAttributes) {
    	super();
        this.type = type;
        this.name = name;
        this.active = active;
        this.createdTimeStamp = createdTimeStamp;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.itemAttributes = itemAttributes;
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

	public boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimeStamp() {
		return createdTimeStamp;
	}

	public void setCreatedTimeStamp(Date createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}
	
	@Lob // use really big value type in the table
	public String getItemAttributes() {
		return itemAttributes;
	}

	public void setItemAttributes(String itemAttributes) {
		this.itemAttributes = itemAttributes;
	}
	
	@ManyToOne(fetch = FetchType.LAZY//EAGER //, optional = true
			/* DO NOT CASCADE ANY OPERATION ON ORIGINAL: , cascade = CascadeType.ALL*/)
	public ItemEntity getParent() {
		return parent;
	}

	public void setParent(ItemEntity parent) {
		this.parent = parent;
	}
	
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY//EAGER 
			/* DO NOT CASCADE ANY OPERATION ON RESPONSES: , cascade = CascadeType.ALL*/)
	public Set<ItemEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<ItemEntity> children) {
		this.children = children;
	}

	public void addChild(ItemEntity child) {
		this.children.add(child);
	}
	
	@Id
	public String getSpaceItemId() {
		return spaceItemId;
	}

	public void setSpaceItemId(String spaceItemId) {
		this.spaceItemId = spaceItemId;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((spaceItemId == null) ? 0 : spaceItemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ItemEntity))
			return false;
		ItemEntity other = (ItemEntity) obj;
		if (spaceItemId == null) {
			if (other.spaceItemId != null)
				return false;
		} else if (!spaceItemId.equals(other.spaceItemId))
			return false;
		return true;
	}

	public String getUserSpace() {
		return userSpace;
	}

	public void setUserSpace(String userSpace) {
		this.userSpace = userSpace;
	}
	
	
}
