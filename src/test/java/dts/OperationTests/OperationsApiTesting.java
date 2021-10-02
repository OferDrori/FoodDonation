package dts.OperationTests;

import org.springframework.web.client.RestTemplate;

import dts.boundaries.OperationBoundary;

public class OperationsApiTesting extends OperationTestUtils {

	private RestTemplate restTemplate;

	public OperationsApiTesting(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void deleteAllOperations(String url, String adminSpace, String adminEmail) {
		this.restTemplate.delete(url, adminSpace, adminEmail);
	}

	public OperationBoundary[] getAllOperations(String url, String adminSpace, String adminEmail) {
		return this.restTemplate.getForObject(url, OperationBoundary[].class, adminSpace, adminEmail);
	}

	protected Object invokeOperationOnItem(String url, OperationBoundary operationBoundary) {
		String invokeOperationUrl = makeUrl(url, OperationTestConstants.INVOKE_OPERATION_URL);
		return this.restTemplate.postForObject(invokeOperationUrl, operationBoundary, Object.class);
	}

	public String makeUrl(String url, String... values) {
		return super.makeUrl(url, values);
	}
}
