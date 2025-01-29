package ru.smax.social.network.friend;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<Integer> getSubscriberIds(Integer authorId) {
        return friendRepository.findFollowersIds(authorId);
    }

    @Transactional(readOnly = true)
    public List<Integer> getAuthors(Integer followerId) {
        return friendRepository.findAuthors(followerId);
    }
}
