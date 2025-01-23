package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@AllArgsConstructor
@Service
public class PostCacheService {
    private RedisTemplate<String, UUID> feedRedisTemplate;
    private RedisTemplate<UUID, Post> postRedisTemplate;

    public List<Post> getFeed(Integer userId, Integer offset, Integer limit) {
        List<UUID> postIds = feedRedisTemplate.opsForList().range(
                keyFeed(userId),
                offset,
                offset + limit - 1
        );
        log.debug("Got postIds from cache [total={}, from={}, to={}]",
                postIds == null ? 0 : postIds.size(),
                offset,
                offset + limit - 1
        );
        if (postIds.isEmpty()) {
            return List.of();
        }

        List<Post> posts = postRedisTemplate.opsForValue().multiGet(postIds);
        log.debug("Got posts from cache [total={}, from={}, to={}]",
                posts.size(),
                offset,
                offset + limit - 1
        );

        return posts;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void putFeed(Integer userId, List<Post> posts) {
        Map<UUID, Post> idToPost = posts.stream()
                                        .collect(toMap(
                                                Post::id,
                                                identity()
                                        ));

        feedRedisTemplate.opsForList().rightPushAll(
                keyFeed(userId),
                idToPost.keySet()
        );
        log.debug("Put post feed into redis: user-id={}, feed-size={}", userId, posts.size());

        postRedisTemplate.opsForValue().multiSetIfAbsent(idToPost);
        log.debug("Put posts into redis (total {})", posts.size());

        // TODO сделать поддержку не более 100 постов в кэше
    }

    private String keyFeed(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        return "posts:feed:%d".formatted(userId);
    }

    private String keyPost(UUID postId) {
        if (postId == null) {
            throw new IllegalArgumentException("postId is null");
        }
        return "post:%s".formatted(postId);
    }
}
