package ru.smax.social.network.post.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.smax.social.network.friend.FriendService;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WebSocketHandler extends TextWebSocketHandler {
    private static final String WS_SESSION_ATTR_USER_ID = "userId";

    private final RabbitTemplate rabbitTemplate;
    private final FriendService friendService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.debug("WebSocket session opened: {}", session);

        var userId = parseUserIdAndSetAttribute(session);
        createQueueAndConsumer(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.debug("WebSocket session closed: {}, status='{}'", session, status);
        var userId = (Integer) session.getAttributes().getOrDefault(WS_SESSION_ATTR_USER_ID, null);
        if (userId != null) {
            String queueName = toQueueName(userId);
            rabbitTemplate.execute(ch -> ch.queueDelete(queueName));
            log.debug("Deleted queue '{}'", queueName);
        }
    }

    private void createQueueAndConsumer(Integer userId, WebSocketSession session) {
        var name = toQueueName(userId);
        rabbitTemplate.execute(ch -> {
            ch.queueDeclare(name, false, true, true, null);
            log.debug("Created queue '{}'", name);

            var authors = friendService.getAuthors(userId);
            for (var author : authors) {
                var routingKey = RabbitMQConfig.toPostAuthorRoutingKey(author);
                ch.queueBind(
                        name,
                        RabbitMQConfig.EXCHANGE_POSTS,
                        routingKey
                );
                log.trace("Created queue binding: user-id={}, routing-key={}", userId, routingKey);
            }
            log.debug("Created {} queue bindings: user-id={}", authors.size(), userId);

            ch.basicConsume(
                    name,
                    true,
                    (_, message) -> {
                        var wsMessage = new String(message.getBody());
                        session.sendMessage(new TextMessage(wsMessage));
                        log.debug("Sent message to websocket: user-id={}, message='{}'", userId, wsMessage);
                    },
                    _ -> {
                    }
            );

            return null;
        });
    }

    private Integer parseUserIdAndSetAttribute(WebSocketSession session) {
        Supplier<IllegalArgumentException> exceptionSupplier = () -> new IllegalArgumentException("No query param 'userId'");

        var queryParams = Optional.of(session)
                                  .map(WebSocketSession::getUri)
                                  .map(URI::getQuery)
                                  .map(s -> s.split("&"))
                                  .orElseThrow(exceptionSupplier);

        for (String queryParam : queryParams) {
            if (queryParam.startsWith("userId=")) {
                var userId = Integer.parseInt(queryParam.substring("userId=".length()));
                session.getAttributes().put(WS_SESSION_ATTR_USER_ID, userId);
                return userId;
            }
        }
        throw exceptionSupplier.get();
    }

    private static String toQueueName(Integer userId) {
        return "user.queue." + userId;
    }
}
