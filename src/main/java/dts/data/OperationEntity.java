package dts.data;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="OPERATIONS")
public class OperationEntity {

    private String type;
    private String itemId;
    private Date createdTimeStamp;
    private String email;
    private String operationAttributes;
    private String spaceAndOperationId;
    private String itemSpace;
    private String userSpace;
    
    
    public OperationEntity() {
    	
    }

	public OperationEntity(String type, String itemId, Date createdTimeStamp, String email,
			String operationsAttributes) {
		super();
		this.type = type;
		this.itemId = itemId;
		this.createdTimeStamp = createdTimeStamp;
		this.email = email;
		this.operationAttributes = operationsAttributes;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getItemId() {
		return itemId;
	}


	public void setItemId(String itemId) {
		this.itemId = itemId;
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

	@Lob // use really big value type in the table
	public String getOperationAttributes() {
		return operationAttributes;
	}


	public void setOperationAttributes(String operationsAttributes) {
		this.operationAttributes = operationsAttributes;
	}

	@Id
	public String getSpaceAndOperationId() {
		return spaceAndOperationId;
	}

	public void setSpaceAndOperationId(String spaceAndOperationId) {
		this.spaceAndOperationId = spaceAndOperationId;
	}

	public String getItemSpace() {
		return itemSpace;
	}

	public void setItemSpace(String itemSpace) {
		this.itemSpace = itemSpace;
	}

	public String getUserSpace() {
		return userSpace;
	}

	public void setUserSpace(String userSpace) {
		this.userSpace = userSpace;
	}
	
}
