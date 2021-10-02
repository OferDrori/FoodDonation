package dts.ItemTests;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import dts.boundaries.ItemBoundary;

public class ItemsApiTesting {

	private RestTemplate restTemplate;

	public ItemsApiTesting(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public ItemBoundary createItem(String url, ItemBoundary itemBoundary) {
		return this.restTemplate.postForObject(url, itemBoundary, ItemBoundary.class);
	}

	public void updateItem(String url, ItemBoundary itemBoundary) {
		this.restTemplate.put(url, itemBoundary);
	}

	public ItemBoundary getItem(String url, String userSpace, String userEmail, String itemSpace, String itemId) {
		return this.restTemplate.getForObject(url, ItemBoundary.class, userSpace, userEmail, itemSpace, itemId);
	}

	public ItemBoundary[] getAllItems(String url, String adminSpace, String adminEmail) {
		return this.restTemplate.getForObject(url, ItemBoundary[].class, adminSpace, adminEmail);
	}

	public void deleteAllItems(String url, String adminSpace, String adminEmail) {
		this.restTemplate.delete(url, adminSpace, adminEmail);
	}
}
