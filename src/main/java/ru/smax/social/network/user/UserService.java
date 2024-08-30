package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final ConcurrentHashMap<String, UserView> idToUser = new ConcurrentHashMap<>();

    public void registerUser(UserController.RegisterUserRequest request) {
        if (idToUser.containsKey(request.id())) {
            throw new IllegalArgumentException("User with id %s already exists".formatted(request.id()));
        }

        idToUser.putIfAbsent(
                request.id(),
                UserView.builder()
                        .id(request.id())
                        .name(request.name())
                        .password(request.password())
                        .build()
        );
    }

    public UserView findUser(String id) {
        return idToUser.get(id);
    }
}
