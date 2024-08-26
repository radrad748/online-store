package rad.online.store.entities.dto;

import lombok.Builder;
import lombok.Data;
import rad.online.store.entities.Image;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private Image avatar;
    private LocalDateTime dateCreated;
}
