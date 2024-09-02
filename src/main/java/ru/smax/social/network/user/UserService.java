package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final ConcurrentHashMap<String, User> usernameToUser = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public UserDetailsService userDetailsService() {
        return this::findByUsername;
    }

    public User registerUser(UserController.RegisterUserRequest request) {
        var username = request.username();
        if (usernameToUser.containsKey(username)) {
            throw new IllegalArgumentException("User with username '%s' already exists".formatted(username));
        }

        var encoded = passwordEncoder.encode(request.password());
        var newUser = User.builder()
                          .username(username)
                          .password(encoded)
                          .build();
        usernameToUser.putIfAbsent(username, newUser);
        return newUser;
    }

    public User findByUsername(String username) {
        return usernameToUser.get(username);
    }

    public List<User> findUsers() {
        return usernameToUser.values()
                             .stream()
                             .toList();
    }
}
