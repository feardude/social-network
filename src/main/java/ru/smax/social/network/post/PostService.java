package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class PostService {
    private final PostCacheService postCacheService;
    private final PostRepository repository;

    @Transactional(readOnly = true)
    public Post findPost(UUID postId) {
        return repository.findById(postId);
    }

    @Transactional(readOnly = true)
    public List<Post> getFeed(Integer userId, Integer offset, Integer limit) {
        var cached = postCacheService.getFeed(userId, offset, limit);
        if (!cached.isEmpty()) {
            return cached;
        }

        log.debug("No posts in cache, loading from DB [user-id={}]", userId);
        var posts = repository.findFriendsPosts(userId);
        log.debug("Found {} posts", posts.size());
        if (!posts.isEmpty()) {
            postCacheService.putFeed(userId, posts);
        }
        return posts;
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<UUID>> findFriendsPostIds(List<Integer> userIds) {
        log.info("Looking up feed for {} user ids", userIds.size());
        return repository.findFriendPostIds(userIds);
    }

    public List<Post> findPosts(List<UUID> postIds) {
        return repository.findById(postIds);
    }
}
