package ru.smax.social.network.post;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record Post(
        UUID id,
        String text,
        int authorUserId
) {
}
