package dts.display;

/**
 * This is the a class that represents a 'create user' request from the client.
 */
public class NewUserDetails {
    private String email;
    private String username;
    private UserBoundaryRole role;
    private String avatar;

    public NewUserDetails() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}


