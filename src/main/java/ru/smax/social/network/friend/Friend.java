package ru.smax.social.network.friend;

import java.time.LocalDateTime;

public record Friend(
        Long userId,
        Long friendId,
        LocalDateTime createdAt
) {
}

