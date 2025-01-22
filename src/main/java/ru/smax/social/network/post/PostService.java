package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository repository;

    public Post findPost(UUID postId) {
        return repository.findById(postId);
    }
}
