package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository repository;

    public Post findPost(UUID postId) {
        return repository.findById(postId);
    }

    public List<Post> getFeed(Integer userId, Integer offset, Integer limit) {
        var posts = repository.findFriendsPosts(userId, offset, limit);
        log.info("Found {} posts", posts.size());
        return posts;
    }
}
