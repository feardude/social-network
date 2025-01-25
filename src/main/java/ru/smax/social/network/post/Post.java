package ru.smax.social.network.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Post(
        UUID id,
        String text,
        int authorUserId,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime createdAt
) implements Serializable {
}
