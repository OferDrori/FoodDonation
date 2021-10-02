package dts.logic;

import dts.boundaries.OperationBoundary;
import dts.data.ItemEntity;

public interface FoodDonationOperations {
	
	public ItemEntity invokeOperation(OperationBoundary operation, ItemEntity itemEntity);
}
