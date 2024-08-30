package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE)
    public void registerUser(@RequestBody RegisterUserRequest request) {
        log.info("Registering new user: {}", request);
        userService.registerUser(request);
    }

    @GetMapping("/get/{id}")
    public UserView getUser(@PathVariable("id") String id) {
        log.info("Looking up user [id='{}']", id);
        return userService.findUser(id);
    }

    public record RegisterUserRequest(
            String id,
            String name,
            String password
    ) {
    }
}
