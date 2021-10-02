package dts.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dts.boundaries.UserBoundary;
import dts.display.Food;
import dts.display.NewUserDetails;
import dts.display.PickupTime;
import dts.display.UserBoundaryRole;

public class TestUtils {

	public String makeUrl(String url, String... values) {
		StringBuilder sg = new StringBuilder();

		List<String> list = new ArrayList<String>(Arrays.asList(values));
		list.add(0, url);
		for (String string : list)
			sg.append(string + "/");

		return sg.toString();
	}

	public NewUserDetails createNewUserDetails(String avatar, String email, String userName,
			UserBoundaryRole userRole) {
		NewUserDetails newUser = new NewUserDetails();
		newUser.setAvatar(avatar);
		newUser.setEmail(email);
		newUser.setRole(userRole);
		newUser.setUsername(userName);
		return newUser;
	}

	protected static PickupTime getPickupTimeForTheNextHoursFromNow(int hoursFromNowToStartTime,
			int hoursFromNowToEndTime) {
		PickupTime pickupTime = new PickupTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, hoursFromNowToStartTime);
		Date startTime = calendar.getTime();
		pickupTime.setStart(startTime);
		calendar.add(Calendar.HOUR, hoursFromNowToEndTime - hoursFromNowToStartTime);
		Date endTime = calendar.getTime();
		pickupTime.setEnd(endTime);
		return pickupTime;
	}

	protected void assertFoodsEqual(List<Food> foods1, List<Food> foods2) {
		assertThat(foods1).isNotNull();
		assertThat(foods2).isNotNull();

		assertEquals(foods1.size(), foods2.size());

		for (int i = 0; i < foods1.size(); i++) {
			assertEquals(foods1.get(i).getType(), foods2.get(i).getType());
			assertEquals(foods1.get(i).getNumberOfDishes(), foods2.get(i).getNumberOfDishes());
		}
	}

	public String getUserSpace(UserBoundary userWhichHasASpace) {
		return userWhichHasASpace.getUserId().getSpace();
	}

	public String getUserEmail(UserBoundary userWhichHasAnEmail) {
		return userWhichHasAnEmail.getUserId().getEmail();
	}

}
