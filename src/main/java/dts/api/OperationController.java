package dts.api;

import dts.boundaries.OperationBoundary;
import dts.logic.OperationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OperationController {
	
	private OperationService operationHandler;
	
    @Autowired
    public void setOperationHandler(OperationService operationServiceImplementation) {
        this.operationHandler = operationServiceImplementation;
    }
    /**
     * api for manipulate items using operations(operation boundary), HTTP request type: POST
     *
     * @param operationBoundary
     * @return for now, a simple json
     */
    @RequestMapping(
            path = "/dts/operations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object invokeOperationOnItem(@RequestBody OperationBoundary operationBoundary) {
        return operationHandler.invokeOperation(operationBoundary);
    }
    
}
