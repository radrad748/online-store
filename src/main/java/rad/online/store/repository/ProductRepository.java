package rad.online.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import rad.online.store.entities.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.title = :title order by p.dateCreated DESC")
    List<Product> findByTitle(@Param("title") String title);

    @Query("select p from Product p where p.brand = :brand order by p.dateCreated DESC")
    List<Product> findByBrand(@Param("brand") String brand);

    @Query("select p from Product p where (p.city = :city and p.title = :title) or (p.city = :city and p.model = :title) order by p.dateCreated DESC")
    Page<Product> findByCityAndTitle(@Param("city") String city, @Param("title") String title, Pageable pageable);

    @Query("select p from Product p where p.city = :city and p.brand = :brand order by p.dateCreated DESC")
    Page<Product> findByCityAndBrand(@Param("city") String city, @Param("brand") String brand, Pageable pageable);

    @Query("select p from Product p where p.city = :city order by p.dateCreated DESC")
    Page<Product> findByCity(@Param("city") String city, Pageable pageable);

    @Query("select p from Product p where p.user.email = :email order by p.dateCreated DESC")
    List<Product> findProductsByUserEmailOrderDesc(@Param("email") String email);


    @Query("select p from Product p where p.title = :title or p.brand = :title or p.model = :title order by p.dateCreated DESC")
    Page<Product> findByTitleOrBrandOrModel(@Param("title") String title, Pageable pageable);

    @Query("select p from Product p order by p.dateCreated DESC")
    List<Product> findAll();

    @Query("select count(p) from Product p where p.title = :title or p.model = :title or p.brand = :title")
    long countByTitle(@Param("title") String title);

    @Query("select count(p) from Product p where p.city = :city")
    long countByCity(@Param("city") String city);

    @Query("select count(p) from Product p where (p.title = :title or p.model = :title or p.brand = :title) and p.city = :city")
    long countByCityAndTitle(@Param("city") String city, @Param("title") String title);

    @Query("select p from Product p order by p.dateCreated DESC")
    Page<Product> findAll(Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from Product p where p.id = :id")
    void deleteById(@Param("id") Long id);
}
