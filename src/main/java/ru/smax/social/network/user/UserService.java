package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return this::findByUsername;
    }

    public User registerUser(UserController.RegisterUserRequest request) {
        log.info("Registering new user: {}", request.username());
        var username = request.username();
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("User with username '%s' already exists".formatted(username));
        }

        var encoded = passwordEncoder.encode(request.password());
        var newUser = User.builder()
                          .username(username)
                          .password(encoded)
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
