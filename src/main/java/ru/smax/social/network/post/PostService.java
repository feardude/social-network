package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository repository;

    @Transactional(readOnly = true)
    public Post findPost(UUID postId) {
        return repository.findById(postId);
    }

    @Transactional(readOnly = true)
    public List<Post> getFeed(Integer userId, Integer offset, Integer limit) {
        return repository.findFriendsPosts(userId, offset, limit);
    }
}
