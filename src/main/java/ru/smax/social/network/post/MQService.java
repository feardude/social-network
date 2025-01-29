package ru.smax.social.network.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.smax.social.network.post.ws.RabbitMQConfig;

@Slf4j
@RequiredArgsConstructor
@Service
public class MQService {
    private final RabbitTemplate rabbitTemplate;

    @Async
    public void sendNewPost(Post newPost) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_POSTS,
                RabbitMQConfig.toPostAuthorRoutingKey(newPost.authorUserId()),
                newPost
        );
    }
}
