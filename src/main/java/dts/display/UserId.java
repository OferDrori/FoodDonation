package dts.display;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dts.errors.BadRequest;

/**
 * This class represents a userID parameters such as email and space.
 */
public class UserId {
    private String space;
    private String email;

    /**
     * Init the UserBoundary object.
     *
     * @param userEmail - user email.
     * @param userSpace - user space.
     */
    public UserId(String userSpace, String userEmail) {
        this.space = userSpace;
        this.setEmail(userEmail);
    }

    public UserId() {

    }

    public String getEmail() {
        return email;
    }

    public String getSpace() {
        return space;
    }

    public void setEmail(String email) {
    	if (isValidEmail(email))
    		this.email = email;
    	else
    		throw new BadRequest("Email is invalid!!!!!");
    }

    public void setSpace(String space) {
        this.space = space;
    }
    
    /**
	 * Given an email, validates if the email is in a correct form.
	 *
	 * @param email: email address.
	 * 
	 * @return true if email is valid, false otherwise.
	 */
	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern p = Pattern.compile(emailRegex);
		Matcher m = p.matcher(email);
		return m.matches();
	}
    
	public String concatenateUserSpaceAndEmail() {
		return space + ":" + email; 
	}
	
}
