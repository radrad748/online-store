package rad.online.store.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rad.online.store.entities.Image;
import rad.online.store.repository.ImageRepository;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public Image findById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }
}
