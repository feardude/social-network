package ru.smax.social.network.dialog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.smax.social.network.dialog.dto.DialogResponse;
import ru.smax.social.network.dialog.dto.SendMessageRequest;

@FeignClient(
        name = "dialogs-client",
        url = "http://localhost:8081/api/internal/v1/dialog",
        configuration = FeignClientConfig.class
)
public interface DialogClient {

    @GetMapping("/{from}/{to}")
    DialogResponse findDialogs(@PathVariable Integer from,
                               @PathVariable Integer to);

    @PostMapping
    void sendMessage(@RequestBody SendMessageRequest request);
}
