package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {
    private final PostCacheWarmupService cacheWarmupService;
    private final PostService postService;

    @GetMapping("/{postId}")
    public Post getPost(@PathVariable UUID postId) {
        return postService.findPost(postId);
    }

    @GetMapping("/feed")
    public FeedResponse getFeed(@RequestParam(value = "userId") Integer userId, // для простоты тестирования без аутентификации
                                @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        log.debug("Requested post feed: user-id={}, offset={}, limit={}", userId, offset, limit);
        return new FeedResponse(
                postService.getFeed(userId, offset, limit)
        );
    }

    @PostMapping
    public void createPost(@RequestBody CreatePostRequest request) {
        log.debug("Requested create post: {}", request);
        postService.createPost(request);
    }

    @PostMapping("/cache")
    public void rebuildCache() {
        log.debug("Requested cache rebuild");
        cacheWarmupService.warmUpCache();
    }

    public record FeedResponse(
            List<Post> posts
    ) {
    }

    public record CreatePostRequest(
            int userId,
            String text
    ) {
    }
}
