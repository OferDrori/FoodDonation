package dts.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import dts.display.UserRole;

@Entity
@Table(name = "USERS")
public class UserEntity {

	private String username;
	private String avatar;
	private UserRole role;
	private String spaceAndEmail;

	public UserEntity() {
	}

	public UserEntity(String username, String avatar, UserRole userRole, String spaceAndEmail) {
		this.username = username;
		this.avatar = avatar;
		this.role = userRole;
		this.spaceAndEmail = spaceAndEmail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	@Id
	public String getSpaceAndEmail() {
		return spaceAndEmail;
	}

	public void setSpaceAndEmail(String email) {
		this.spaceAndEmail = email;
	}
}
