package dts;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import dts.boundaries.ItemBoundary;
import dts.boundaries.OperationBoundary;
import dts.boundaries.UserBoundary;
import dts.display.NewUserDetails;
import dts.display.UserId;

public class Utils {

	public Object[] fromObjectListToObjectArray(List<Object> list) {
		Object[] objects = new Object[list.size()];
		return list.toArray(objects);
	}

	public UserBoundary fromNewUserDetailsToUserBoundary(NewUserDetails newUserDetails) {
		return new UserBoundary(new UserId(null, newUserDetails.getEmail()), newUserDetails.getUsername(),
				newUserDetails.getAvatar(), newUserDetails.getRole());
	}

	public UserBoundary[] fromUserBoundaryListToArray(List<UserBoundary> list) {
		Object[] objects = this.fromObjectListToObjectArray(new ArrayList<>(list));
		UserBoundary[] userBoundaries = new UserBoundary[list.size()];

		for (int i = 0; i < list.size(); i++)
			userBoundaries[i] = (UserBoundary) objects[i];
		return userBoundaries;
	}

	public ItemBoundary[] fromItemBoundaryListToArray(List<ItemBoundary> list) {
		Object[] objects = this.fromObjectListToObjectArray(new ArrayList<>(list));
		ItemBoundary[] itemBoundaries = new ItemBoundary[list.size()];

		for (int i = 0; i < list.size(); i++)
			itemBoundaries[i] = (ItemBoundary) objects[i];
		return itemBoundaries;
	}

	public OperationBoundary[] fromOperationBoundaryListToArray(List<OperationBoundary> list) {
		Object[] objects = this.fromObjectListToObjectArray(new ArrayList<>(list));
		OperationBoundary[] OperationBoundaries = new OperationBoundary[list.size()];

		for (int i = 0; i < list.size(); i++)
			OperationBoundaries[i] = (OperationBoundary) objects[i];
		return OperationBoundaries;
	}

	/**
	 * Given an object, validates if it is null
	 *
	 * @param obj:
	 *            the object to validate.
	 * 
	 * @return true if object is null, False otherwise.
	 */
	public boolean isObjectNull(Object obj) {
		return obj == null;
	}

	/**
	 * Given a string, validates if the string is empty
	 *
	 * @param str:
	 *            the string to validate.
	 * 
	 * @return true if string is empty, False otherwise.
	 */
	public boolean isStringEmpty(String str) {
		return str.equals("");
	}

	public boolean areStringsEqual(String first, String second) {
		return first.equals(second);
	}

	public int compareDates(Date startDate, Date endDate) {
		return endDate.compareTo(startDate);
	}
}
