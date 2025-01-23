package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.smax.social.network.user.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@AllArgsConstructor
@Service
public class PostCacheWarmupListener implements ApplicationListener<ApplicationReadyEvent> {
    private final PostCacheService postCacheService;
    private final PostService postService;
    private final UserService userService;

    @Async
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Started post cache warmup");

        // GET last recently active users
        // for each 1000 users (batch) - get feed and put to cache
        var to = LocalDate.now();
        var from = to.minusDays(1);
        var userIds = userService.findActiveAfter(from, to);
        log.info("Found {} active users ({} - {})", userIds.size(), from, to);

        var batchSize = 1000;
        var threads = 4;

        Set<UUID> loadedPosts = ConcurrentHashMap.newKeySet();

        try (var executor = newFixedThreadPool(threads)) {
            for (int i = 0; i < userIds.size(); i += batchSize) {
                int end = Math.min(i + batchSize, userIds.size());
                List<Integer> batchUserIds = userIds.subList(i, end);
                executor.submit(() ->
                        processUserBatch(batchUserIds, loadedPosts)
                );
            }

            executor.shutdown();
        }

        log.info("Finished post cache warmup");
    }

    private void processUserBatch(List<Integer> batchUserIds, Set<UUID> loadedPosts) {
        Map<Integer, List<UUID>> feed = postService.findFriendsPostIds(batchUserIds);

        long totalPosts = feed.values()
                              .stream()
                              .flatMap(Collection::stream)
                              .distinct()
                              .count();
        log.info("Batch: processing {} users, {} unique posts", batchUserIds.size(), totalPosts);

        List<UUID> postIds = feed.values()
                                 .stream()
                                 .flatMap(Collection::stream)
                                 .distinct()
                                 .filter(postId -> !loadedPosts.contains(postId))
                                 .toList();
        log.info("Loaded posts={}, should load {} new posts", loadedPosts.size(), postIds.size());

        int batchSize = 1_000;
        for (int i = 0; i < postIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, postIds.size());
            List<UUID> batchPostIds = postIds.subList(i, end)
                                             .stream()
                                             .filter(postId -> !loadedPosts.contains(postId))
                                             .toList();
            log.info("Loading and caching {} posts (originally {})", batchPostIds.size(), end - i);

            var posts = postService.findPosts(batchPostIds);
            postCacheService.putPosts(posts);
            loadedPosts.addAll(batchPostIds);
        }

        postCacheService.putFeed(feed);
        log.info("Batch: finished loading of {} feeds", feed.size());
    }
}
