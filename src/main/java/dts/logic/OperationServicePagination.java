package dts.logic;

import java.util.List;

import dts.boundaries.OperationBoundary;

public interface OperationServicePagination extends OperationService{
	
	public List<OperationBoundary> getAllOperationsWithPagination(String adminSpace, String adminEmail, int size, int page); 
	
}
