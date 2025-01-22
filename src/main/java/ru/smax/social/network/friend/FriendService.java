package ru.smax.social.network.friend;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;

    public void createFriendship(Long userId, Long friendId) {
        friendRepository.createFriendship(userId, friendId);
    }

    public List<Friend> getFriends(Long userId) {
        return friendRepository.findByUserId(userId);
    }

    public void deleteFriendship(Long userId, Long friendId) {
        friendRepository.deleteFriendship(userId, friendId);
    }
}
