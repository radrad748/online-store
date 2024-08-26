package rad.online.store.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {

    public Product() {
        images = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 20, message = "Title must be between 1 and 20 characters")
    @Pattern(regexp = ".*\\S.*", message = "Title must not be blank")
    private String title;
    @NotNull(message = "Brand cannot be null")
    @Size(min = 1, max = 20, message = "Brand must be between 1 and 20 characters")
    @Pattern(regexp = ".*\\S.*", message = "Brand must not be blank")
    private String brand;
    @NotNull(message = "Model cannot be null")
    @Size(min = 1, max = 20, message = "Model must be between 1 and 20 characters")
    @Pattern(regexp = ".*\\S.*", message = "Model must not be blank")
    private String model;
    @Size(max = 255, message = "Description must be at most 255 characters")
    @Column(columnDefinition = "text")
    private String description;
    @NotNull(message = "Price cannot be null")
    @Column(nullable = false, scale = 2, precision = 10)
    private BigDecimal price;
    @Size(max = 20, message = "City must be at most 20 characters")
    private String city;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<Image> images;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    private Long previewImageId;
    @CreationTimestamp
    private LocalDateTime dateCreated;

    public void addImageToProduct(Image image) {
        image.setProduct(this);
        images.add(image);
    }
}
