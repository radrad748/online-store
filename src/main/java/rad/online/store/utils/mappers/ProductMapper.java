package rad.online.store.utils.mappers;

import rad.online.store.Exceptions.EntityException;
import rad.online.store.entities.Product;
import rad.online.store.entities.dto.ProductDto;

public class ProductMapper {

    public static ProductDto getProductDto(Product product) {
        if (product == null) throw new EntityException("Product don't must be null");

        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .brand(product.getBrand())
                .model(product.getModel())
                .description(product.getDescription())
                .price(product.getPrice())
                .city(product.getCity())
                .images(product.getImages())
                .previewImageId(product.getPreviewImageId())
                .dateCreated(product.getDateCreated())
                .user(UserMapper.getUserDto(product.getUser()))
                .build();
    }

}
