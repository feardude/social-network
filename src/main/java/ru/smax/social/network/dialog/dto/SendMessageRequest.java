package ru.smax.social.network.dialog.dto;

public record SendMessageRequest(
        Integer from,
        Integer to,
        String text
) {
}
