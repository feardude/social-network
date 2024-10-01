package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable("id") String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/search")
    public UsersResponse searchUsers(@RequestParam("first_name") String firstName,
                                     @RequestParam("last_name") String lastName) {
        return new UsersResponse(
                userService.findByName(firstName + "%", lastName + "%")
        );
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.findUsers();
    }

    public record UsersResponse(
            List<User> users
    ) {
    }
}
