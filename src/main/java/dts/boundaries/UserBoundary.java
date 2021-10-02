package dts.boundaries;

import dts.display.UserBoundaryRole;
import dts.display.UserId;

/**
 * This is the a class that represents a user API response to the client.
 */
public class UserBoundary {
    private String username;
    private String avatar;
    private UserBoundaryRole role;
    private UserId userId;

    public UserBoundary() {

    }

    /**
     * Init the UserBoundary object.
     *
     * @param userId   - user ID object.
     * @param username - user name.
     * @param avatar   - user avatar.
     * @param role     - user role. e.g.: PLAYER
     */
    public UserBoundary(UserId userId, String username, String avatar, UserBoundaryRole role) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.role = role;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserBoundaryRole getRole() {
        return role;
    }

    public void setRole(UserBoundaryRole role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserBoundary{" +
                "username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", role=" + role +
                '}';
    }
}
