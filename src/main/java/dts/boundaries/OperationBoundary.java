package dts.boundaries;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dts.display.InvokedBy;
import dts.display.Item;
import dts.display.OperationId;

/**
 * This is the base class for a operation boundary API requests.
 */
public class OperationBoundary {

    private OperationId operationId;
    private String type;
    private Item item;
    private Date createdTimestamp;
    private InvokedBy invokedBy;
    private Map<String, Object> operationAttributes;

    public OperationBoundary() {
    	
    }

    public OperationBoundary(OperationId operationId, String type,
                             Item item, Date timeStamp, InvokedBy invokedBy,
                             HashMap<String, Object> operationsAttributes) {
        this.operationId = operationId;
        this.type = type;
        this.item = item;
        this.createdTimestamp = timeStamp;
        this.invokedBy = invokedBy;
        this.operationAttributes = operationsAttributes;
    }

    
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public OperationId getOperationId() {
        return operationId;
    }

    public void setOperationId(OperationId operationId) {
        this.operationId = operationId;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimeStamp) {
        this.createdTimestamp = createdTimeStamp;
    }

    public InvokedBy getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(InvokedBy invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Map<String, Object> getOperationAttributes() {
        return operationAttributes;
    }

    public void setOperationAttributes(Map<String, Object> operationsAttributes) {
        this.operationAttributes = operationsAttributes;
    }
}
