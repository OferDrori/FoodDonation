package dts.logic;

import dts.boundaries.UserBoundary;
import dts.data.UserEntity;
import dts.display.UserBoundaryRole;
import dts.display.UserId;
import dts.display.UserRole;

import org.springframework.stereotype.Component;

/**
 * This class represents a class that should convert between Boundary and Entity user objects.
 */
@Component
public class UserConverter {

    /**
     * Converts from UserBoundary object to a UserEntity object.
     *
     * @param userBoundary: a UserBoundary object.
     * @return UserEntity object that matches the UserBoundary object.
     */
    public UserEntity toEntity(UserBoundary userBoundary) {
        UserEntity entity = new UserEntity();

        entity.setAvatar(userBoundary.getAvatar());

        if (userBoundary.getRole() != null) {
            entity.setRole(UserRole.valueOf(userBoundary.getRole().name()));
        }
        
        //TODO Check if in the DB we should use the pattern userSpace:userEmail or only userEmail
        entity.setSpaceAndEmail(userBoundary.getUserId().concatenateUserSpaceAndEmail());

        entity.setUsername(userBoundary.getUsername());

        return entity;
    }

    /**
     * Converts from UserEntity object to a UserBoundary object.
     *
     * @param userEntity: a UserEntity object.
     * @return UserBoundary object that matches the UserEntity object.
     */
    public UserBoundary toBoundary(UserEntity userEntity) {

        UserBoundary boundary = new UserBoundary();

        boundary.setAvatar(userEntity.getAvatar());

        if (userEntity.getRole() != null) {
            boundary.setRole(UserBoundaryRole.valueOf(userEntity.getRole().name()));
        }

        boundary.setUserId(this.fromSpaceAndEmailToUserID(userEntity.getSpaceAndEmail()));

        boundary.setUsername(userEntity.getUsername());

        return boundary;
    }

    private UserId fromSpaceAndEmailToUserID(String email) {
        if (email != null) {
            String[] args = email.split(":");
            return new UserId(args[0], args[1]);
        } else {
            return null;
        }
    }
}
