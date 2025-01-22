package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.smax.social.network.user.User;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")
    public Post getFeed(@PathVariable UUID postId) {
        return postService.findPost(postId);
    }

    @GetMapping("/feed")
    public Post getFeed(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                        @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        log.info("Requested post feed: offset={}, limit={}", offset, limit);
        return null;
    }

    public record UsersResponse(
            List<User> users
    ) {
    }
}
