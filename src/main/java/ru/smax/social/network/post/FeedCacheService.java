package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@AllArgsConstructor
@Service
public class FeedCacheService {
    private RedisTemplate<String, Post> redisTemplate;

    public List<Post> get(Integer userId, long offset, long limit) {
        List<Post> posts = redisTemplate.opsForList()
                                        .range(generateKey(userId),
                                                offset,
                                                offset + limit - 1);
        log.debug("Got posts from cache [total={}, from={}, to={}]",
                posts == null ? 0 : posts.size(),
                offset,
                offset + limit - 1
        );
        return posts;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void put(Integer userId, List<Post> posts) {
        redisTemplate.opsForList()
                     .rightPushAll(generateKey(userId), posts);
        log.debug("Put posts into redis: user-id={}, posts-size={}", userId, posts.size());
    }

    private String generateKey(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        return "posts:feed:%d".formatted(userId);
    }
}
