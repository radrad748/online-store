package rad.online.store.entities.dto;

import lombok.Builder;
import lombok.Data;
import rad.online.store.entities.Image;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductDto {
    private Long id;
    private String title;
    private String brand;
    private String model;
    private String description;
    private BigDecimal price;
    private String city;
    private List<Image> images;
    private Long previewImageId;
    private LocalDateTime dateCreated;
    private UserDto user;

}
