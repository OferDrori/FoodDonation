package dts.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import dts.Jackson;
import dts.boundaries.OperationBoundary;
import dts.data.OperationEntity;
import dts.display.InvokedBy;
import dts.display.Item;
import dts.display.OperationId;
import dts.display.UserId;

@Component
public class OperationConverter {
	

	private Jackson jackson;
	
	@Autowired
	public OperationConverter(Jackson jackson) {
		this.jackson = jackson;
	}
	
    public OperationEntity toEntity(OperationBoundary newOperation) {
    	
        OperationEntity entity = new OperationEntity();
        //set date
        entity.setCreatedTimeStamp(newOperation.getCreatedTimestamp());
        //set email
        entity.setEmail(newOperation.getInvokedBy().getUserId().getEmail());
        //set Item ID.
        entity.setItemId(newOperation.getItem().getItemId().getId());
        // set Operations Attributes
        entity.setOperationAttributes(this.jackson.fromMapToStringUsingJackson(newOperation.getOperationAttributes()));
        // set operation type
        entity.setType(newOperation.getType());
        // set user space
        entity.setUserSpace(newOperation.getInvokedBy().getUserId().getSpace());
        // set item space
        entity.setItemSpace(newOperation.getItem().getItemId().getSpace());
  
        return entity;
    }

    public OperationBoundary toBoundary(OperationEntity entity) {
    	
        OperationBoundary boundary = new OperationBoundary();
        //set the date
        boundary.setCreatedTimestamp(entity.getCreatedTimeStamp());
        // set invoked by object
        boundary.setInvokedBy(new InvokedBy(new UserId(entity.getUserSpace(), entity.getEmail())));
        // set item ID object.
        boundary.setItem(new Item(entity.getUserSpace(), entity.getItemId()));
        // set operation ID object
        boundary.setOperationId(fromOperationSpaceAndItemIdToOperationIdObject(entity.getSpaceAndOperationId()));
        // set operation attributes map
        boundary.setOperationAttributes(this.jackson.fromStringToMapUsingJackson(entity.getOperationAttributes()));
        // set operation type
        boundary.setType(entity.getType());

        return boundary;
    }
    
    public OperationId fromOperationSpaceAndItemIdToOperationIdObject(String spaceAndOperationId) {
    	 if (spaceAndOperationId != null) {
             String[] args = spaceAndOperationId.split(":");
             return new OperationId(args[0], args[1]);
         } else {
             return null;
         }
    }
   
}
