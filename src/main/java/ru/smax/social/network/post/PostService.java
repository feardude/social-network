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
    private final FeedCacheService feedCacheService;
    private final PostRepository repository;

    @Transactional(readOnly = true)
    public Post findPost(UUID postId) {
        return repository.findById(postId);
    }

    @Transactional(readOnly = true)
    public List<Post> getFeed(Integer userId, Integer offset, Integer limit) {
        var cached = feedCacheService.get(userId, offset, limit);
        if (!cached.isEmpty()) {
            return cached;
        }

        log.debug("No posts in cached, loading from DB [user-id={}, offset={}, limit={}]", userId, offset, limit);
        var posts = repository.findFriendsPosts(userId);
        log.debug("Found {} posts", posts.size());
        if (!posts.isEmpty()) {
            feedCacheService.put(userId, posts);
        }
        return posts;
    }
}
