package rad.online.store.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rad.online.store.Exceptions.EntityException;
import rad.online.store.entities.Image;
import rad.online.store.entities.Product;
import rad.online.store.entities.User;
import rad.online.store.repository.ProductRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    @Cacheable("products")
    public List<Product> listProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size)).getContent();
    }
    @Cacheable("products")
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void saveProduct(User user, Product product, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        if (product == null || user == null) throw new EntityException("Product and User don't must be null");

        product.setUser(user);

        Image image1 = toImageEntity(product, file1);
        Image image2 = toImageEntity(product, file2);
        Image image3 = toImageEntity(product, file3);
        if (image1 != null) image1.setPreviewImage(true);

        log.info("Saving new Product. Title: {}; Author email: {}", product.getTitle(), product.getUser().getEmail());

        Product productFromDb = productRepository.save(product);
        if (!productFromDb.getImages().isEmpty()) {
            productFromDb.setPreviewImageId(productFromDb.getImages().get(0).getId());
            productRepository.save(productFromDb);
        }
    }

    private Image toImageEntity(Product product, MultipartFile file) throws IOException {
        if (file.getSize() == 0) return null;

        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());

        product.addImageToProduct(image);

        return image;
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        if (id == null) throw new EntityException("Id don't must be null");
        log.info("Delete product with id {}", id);

        productRepository.deleteById(id);
    }

    public Product getProductById(Long id) {
        if (id == null) throw new EntityException("Id don't must be null");

        return productRepository.findById(id).orElse(null);
    }

    public List<Product> findByTitleList(String title) {
        if (title.trim().isEmpty() || Objects.isNull(title)) throw new EntityException("Title must be not null and not empty");

        return productRepository.findByTitle(title);
    }

    public List<Product> findByBrandList(String brand) {
        if (brand.trim().isEmpty() || Objects.isNull(brand)) throw new EntityException("Brand must be not null and not empty");

        return productRepository.findByBrand(brand);
    }

    public List<Product> findByTitleAndCity(String title, String city, int page, int size) {
        if (Objects.isNull(title) || title.trim().isEmpty() || Objects.isNull(city) || city.trim().isEmpty())
            throw new EntityException("Title and city must be not null and not empty");

        List<Product> productsWithCityAndTitle = productRepository.findByCityAndTitle(city, title, PageRequest.of(page, size)).getContent();
        if (productsWithCityAndTitle.isEmpty())
            return productRepository.findByCityAndBrand(city, title, PageRequest.of(page, size)).getContent();

        return productsWithCityAndTitle;
    }

    public List<Product> findByCity(String city, int page, int size) {
        if (Objects.isNull(city) || city.trim().isEmpty()) throw new EntityException("City must be not null and not empty");

        return productRepository.findByCity(city, PageRequest.of(page, size)).getContent();
    }

    public List<Product> findByTitleOrBrandOrModel(String title, int page, int size) {
        if (Objects.isNull(title) || title.trim().isEmpty())
            throw new EntityException("Title must be not null and not empty");

        return productRepository.findByTitleOrBrandOrModel(title, PageRequest.of(page, size)).getContent();
    }

    public List<Product> findProductsByUserEmailOrderDesc(String email) {
        if (Objects.isNull(email) || email.trim().isEmpty())
            throw new EntityException("Email must be not null and not empty");

        return productRepository.findProductsByUserEmailOrderDesc(email);
    }

    @Cacheable("products")
    public long count() {
        return productRepository.count();
    }

    public long count(String city, String title) {
        if ((Objects.isNull(city) || city.isEmpty()) && (Objects.isNull(title) || title.isEmpty()))
            throw new EntityException("City and title are empty or null");

        if (Objects.isNull(city) || city.isEmpty())
            return productRepository.countByTitle(title);

        if (Objects.isNull(title) || title.isEmpty())
            return productRepository.countByCity(city);

        return productRepository.countByCityAndTitle(city, title);
    }
}
