package ru.smax.social.network.user;

import lombok.Builder;

@Builder
public record UserView(
        String id,
        String name,
        String password
) {
}
