package rad.online.store.utils.mappers;

import rad.online.store.Exceptions.EntityException;
import rad.online.store.entities.User;
import rad.online.store.entities.dto.UserDto;

public class UserMapper {

    public static UserDto getUserDto(User user) {
        if (user == null) throw new EntityException("User don't must be null");

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .avatar(user.getAvatar())
                .dateCreated(user.getDateCreated())
                .build();
    }

}
