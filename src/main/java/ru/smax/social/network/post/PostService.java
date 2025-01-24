package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smax.social.network.friend.FriendService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Slf4j
@AllArgsConstructor
@Service
public class PostService {
    private static final int POST_CACHE_LIMIT = 100;

    private final FriendService friendService;
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
        var posts = repository.findFriendsPosts(userId, POST_CACHE_LIMIT);
        log.debug("Found {} posts", posts.size());
        if (!posts.isEmpty()) {
            postCacheService.putFeed(userId, posts);
        }
        return posts.stream()
                    .skip(offset)
                    .limit(limit)
                    .toList();
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<Post>> findFriendsPostIds(List<Integer> userIds) {
        log.info("Looking up feed for {} user ids", userIds.size());
        return repository.findFriendPostIds(userIds);
    }

    public void createPost(PostController.CreatePostRequest request) {
        var newPost = Post.builder()
                          .id(randomUUID())
                          .authorUserId(request.userId())
                          .text(request.text())
                          .createdAt(LocalDateTime.now())
                          .build();
        repository.savePost(newPost);
        postCacheService.updateSubscribersFeeds(newPost);
        log.debug("Saved new post {}", newPost);
    }
}
