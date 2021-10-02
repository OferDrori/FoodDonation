package dts.logic;

import java.util.List;
import dts.boundaries.OperationBoundary;

public interface OperationService {
	
	public Object invokeOperation(OperationBoundary operation);

	public List<OperationBoundary> getAllOperations(String adminSpace, String adminEmail);

	public void deleteAllOperations(String adminSpace, String adminEmail);
}
