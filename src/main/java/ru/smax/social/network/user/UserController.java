package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.smax.social.network.security.JwtService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE)
    public String registerUser(@RequestBody RegisterUserRequest request) {
        log.info("Registering new user: {}", request);
        var newUser = userService.registerUser(request);
        return jwtService.generateToken(newUser);
    }

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable("id") String username) {
        log.info("Looking up user [username='{}']", username);
        return userService.findByUsername(username);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.findUsers();
    }

    public record RegisterUserRequest(
            String username,
            String password
    ) {
    }
}
