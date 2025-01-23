package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smax.social.network.auth.AuthController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return this::findByUsername;
    }

    public User registerUser(AuthController.RegisterUserRequest request) {
        var username = request.username();

        var noUsername = username == null || username.isBlank();
        if (!noUsername && findByUsername(username) != null) {
            throw new IllegalArgumentException("User with username '%s' already exists".formatted(username));
        }

        if (noUsername) {
            username = UUID.randomUUID().toString();
        }

        var encoded = passwordEncoder.encode(request.password());
        var newUser = User.builder()
                          .username(username)
                          .password(encoded)
                          .firstName(request.firstName())
                          .lastName(request.lastName())
                          .birthday(request.birthday())
                          .biography(request.biography())
                          .city(request.city())
                          .gender(request.gender())
                          .build();
        userRepository.save(newUser);
//        log.info("Registered new user: {}", newUser);
        return newUser;
    }

    public User findByUsername(String username) {
//        log.info("Looking up user by username='{}'", username);
        return null;
//        return userRepository.findByUsername(username);
    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public List<User> findByName(String firstName, String lastName) {
        var users = userRepository.findByName(firstName, lastName);
//        log.info("Found users by name pattern [count={}, first='{}', last='{}']", users.size(), firstName, lastName);
        return users;
    }

    public User findById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Integer> findActiveAfter(LocalDate from, LocalDate to) {
        return userRepository.findIdsActiveInRange(from, to);
    }
}
