package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.smax.social.network.user.UserService;

import java.time.LocalDate;
import java.util.List;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@AllArgsConstructor
@Service
public class PostCacheWarmupService {
    private static final int BATCH_SIZE = 1000;
    private static final int THREADS = 4;
    private static final int ACTIVE_FOR_DAYS = 1;

    private final PostCacheService postCacheService;
    private final PostService postService;
    private final UserService userService;

    @Async
    public void warmUpCache() {
        log.info("Started post cache warmup");

        var to = LocalDate.now();
        var from = to.minusDays(ACTIVE_FOR_DAYS);

        var userIds = userService.findActiveAfter(from, to);
        log.info("Found {} active users ({} - {})", userIds.size(), from, to);

        try (var executor = newFixedThreadPool(THREADS)) {
            for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, userIds.size());
                List<Integer> batchUserIds = userIds.subList(i, end);
                executor.submit(() ->
                        postService.findFriendsPostIds(batchUserIds)
                                   .forEach(postCacheService::putFeed)
                );
            }

            executor.shutdown();
        }

        log.info("Finished post cache warmup");
    }
}
