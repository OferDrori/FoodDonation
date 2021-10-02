package dts.api;

import dts.*;
import dts.boundaries.UserBoundary;
import dts.display.NewUserDetails;
//import dts.UserServiceImplementation;
import dts.logic.UsersService;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UsersService userHandler;
    private Utils utils;

    @Autowired
    public void setUserHandler(UsersService userServiceImplementation) {
        this.userHandler = userServiceImplementation;
    }
    
    @PostConstruct
    public void initUtils() {
    	this.utils = new Utils();
    }
    /**
     * Returns a UserBoundary object that represents a user configuration in json.
     * This method implements the 'GET' Http method request of a specified user by URL.
     *
     * @param space (String) - user's space.
     * @param email (string) - user's email.
     * @return UserBoundary object.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/dts/users/login/{userSpace}/{userEmail}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary getUserDetails(
            @PathVariable("userSpace") String space, @PathVariable("userEmail") String email
    ) {
        return userHandler.login(space, email);
    }

    /**
     * Returns a UserBoundary object that represents a user configuration in json.
     * This method implements the 'POST' Http method to create a new user in the server.
     *
     * @param newUserDetails: an object that represents a user new user.
     * @return UserBoundary object.
     */
    @RequestMapping(
            path = "/dts/users",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserBoundary createUser(@RequestBody NewUserDetails newUserDetails) {
        return userHandler.createUser(this.utils.fromNewUserDetailsToUserBoundary(newUserDetails));
    }

    /**
     * This method implements the 'PUT' Http method to update an existing user in the server.
     *
     * @param space:      user's space.
     * @param email:      user's email.
     * @param updatedUser (UserBoundary) - an object that represents a user json update request.
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/dts/users/{userSpace}/{userEmail}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(
            @PathVariable("userSpace") String space,
            @PathVariable("userEmail") String email,
            @RequestBody UserBoundary updatedUser
    ) throws Exception {
        userHandler.updateUser(space, email, updatedUser);
    }
}
