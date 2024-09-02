package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.smax.social.network.auth.AuthController;

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
        log.info("Registering new user: {}, {}", username, request);

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
        return newUser;
    }

    public User findByUsername(String username) {
        log.info("Looking up user by username='{}'", username);
        return userRepository.findByUsername(username);
    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }
}
