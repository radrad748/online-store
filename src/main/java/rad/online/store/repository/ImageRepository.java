package rad.online.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rad.online.store.entities.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
