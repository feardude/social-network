package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@AllArgsConstructor
@Service
public class PostCacheWarmupListener implements ApplicationListener<ApplicationReadyEvent> {
    private final PostCacheWarmupService cacheWarmupService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        cacheWarmupService.warmUpCache();
    }
}
