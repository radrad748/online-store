package rad.online.store.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rad.online.store.Exceptions.EntityException;
import rad.online.store.entities.User;
import rad.online.store.entities.enums.Role;
import rad.online.store.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @CacheEvict(value = {"users", "user"}, allEntries = true)
    @Transactional
    public boolean createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) return false;

        user.setActive(true);
        user.setPassword(encoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        userRepository.save(user);

        log.info("Saving new User with Email: {}", user.getEmail());
        return true;
    }

    @Cacheable("user")
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) throw new EntityException("Email doesn't must be null or empty");

        return userRepository.findByEmail(email);
    }

    @Cacheable("user")
    public User findById(Long id) {
        if (id == null || id < 0L) throw new EntityException("ID doesn't must be null or less than zero");

        return userRepository.findById(id).orElse(null);
    }

    @Cacheable("users")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Cacheable("users")
    public List<User> findAll(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @CacheEvict(value = {"users", "user"}, allEntries = true)
    @Transactional
    public void banUser(Long id) {
        if (id == null || id < 0) return;

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.isActive()) {
                user.setActive(false);
                log.info("Ban user with id = {}; email = {}", user.getId(), user.getEmail());
            }
            else {
                user.setActive(true);
                log.info("Unban user with id = {}; email = {}", user.getId(), user.getEmail());
            }
            userRepository.save(user);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void changeUserRoles(Long id, Map<String, String> form) {
        if (id == null || id < 0) throw new EntityException("Id can't be null or less zero");

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        User user = userRepository.findById(id).orElseThrow();
        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) user.getRoles().add(Role.valueOf(key));
        }
        userRepository.save(user);
    }

    @Cacheable("users")
    public long count() {
        return userRepository.count();
    }

    @CacheEvict(value = {"users", "user", "products"}, allEntries = true)
    @Transactional
    public void deleteById(Long id) {
        if(Objects.isNull(id) || id < 0) throw new EntityException("ID can't be null or below zero");

        userRepository.deleteById(id);
    }
}
