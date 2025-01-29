package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.smax.social.network.friend.FriendService;
import ru.smax.social.network.post.ws.RabbitMQConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.comparing;

@Slf4j
@AllArgsConstructor
@Service
public class PostCacheService {
    private final FriendService friendService;
    private final RedisTemplate<String, UUID> feedRedisTemplate;
    private final RedisTemplate<UUID, Post> postRedisTemplate;

    /**
     * newPost would be added to multiple caches:
     * - post itself
     * - all subscribers' feeds
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = RabbitMQConfig.EXCHANGE_POSTS, type = "topic", durable = "false"),
            value = @Queue(value = "queue.post.cache", durable = "false", autoDelete = "true"),
            key = "post.author.*"
    ))
    public void addPostToFeeds(@Payload Post newPost) {
        log.debug("Started updating feeds for post: hashcode={}, author={}", newPost.id().hashCode(), newPost.authorUserId());

        var subscriberIds = friendService.getSubscriberIds(newPost.authorUserId());
        log.debug("Should update {} feeds", subscriberIds.size());

        double score = newPost.createdAt().toEpochSecond(UTC);
        for (Integer userId : subscriberIds) {
            String key = keyFeed(userId);
            feedRedisTemplate.opsForZSet().add(key, newPost.id(), score);
            feedRedisTemplate.opsForZSet().removeRange(key, 0, -101);
        }
        log.debug("Updated {} cache feeds", subscriberIds.size());

        postRedisTemplate.opsForValue().set(newPost.id(), newPost);
        log.debug("Finished updating feeds for post: hashcode={}, author={}", newPost.id().hashCode(), newPost.authorUserId());
    }

    public List<Post> getFeed(Integer userId, Integer offset, Integer limit) {
        String key = keyFeed(userId);
        Set<UUID> postIds = feedRedisTemplate.opsForZSet()
                                             .reverseRange(key, offset, offset + limit - 1L);

        if (postIds == null || postIds.isEmpty()) {
            return List.of();
        }

        List<Post> posts = postRedisTemplate.opsForValue().multiGet(postIds);
        return posts == null
                ? List.of()
                : posts.stream()
                       .filter(Objects::nonNull)
                       .sorted(comparing(Post::createdAt).reversed())
                       .toList();
    }

    @Async
    public void putFeed(Integer userId, List<Post> feed) {
        String feedKey = keyFeed(userId);
        Map<UUID, Post> postIdToPost = new HashMap<>();

        Set<ZSetOperations.TypedTuple<UUID>> scoredPostIds = new HashSet<>();
        for (Post post : feed) {
            UUID postId = post.id();
            postIdToPost.put(postId, post);

            double score = post.createdAt().toEpochSecond(UTC);
            scoredPostIds.add(new DefaultTypedTuple<>(postId, score));
        }

        feedRedisTemplate.opsForZSet().add(feedKey, scoredPostIds);
        log.debug("Put feed into redis: user-id={}, feed-size={}", userId, feed.size());

        postRedisTemplate.opsForValue().multiSetIfAbsent(postIdToPost);
        log.debug("Put posts into redis (total {})", feed.size());
    }

    private String keyFeed(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        return "posts:feed:%d".formatted(userId);
    }
}
