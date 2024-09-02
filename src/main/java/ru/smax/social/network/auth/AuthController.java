package ru.smax.social.network.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.smax.social.network.user.UserService;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping(value = "/user/register", consumes = APPLICATION_JSON_VALUE)
    public String registerUser(@RequestBody RegisterUserRequest request) {
        var newUser = userService.registerUser(request);
        return jwtService.generateToken(newUser);
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE)
    public JwtTokenResponse loginUser(@RequestBody AuthUserRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username,
                        request.password
                )
        );
        var user = userService.findByUsername(request.username);
        return new JwtTokenResponse(
                jwtService.generateToken(user)
        );
    }

    public record RegisterUserRequest(
            String username,
            String password,

            @JsonProperty("first_name")
            String firstName,

            @JsonProperty("second_name")
            String lastName,

            @JsonProperty("birthdate")
            LocalDate birthday,

            String biography,
            String city,
            String gender
    ) {
        @Override
        public String toString() {
            return "RegisterUserRequest{" +
                    "username='" + username + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", birthday=" + birthday +
                    ", biography='" + biography + '\'' +
                    ", city='" + city + '\'' +
                    ", gender='" + gender + '\'' +
                    '}';
        }
    }

    public record AuthUserRequest(
            String username,
            String password
    ) {
    }

    public record JwtTokenResponse(
            String token
    ) {
    }
}
