package ru.smax.social.network.dialog;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.smax.social.network.dialog.dto.DialogResponse;
import ru.smax.social.network.dialog.dto.SendMessageRequest;

@AllArgsConstructor
@Service
public class DialogService {
    private final DialogClient dialogClient;

    public DialogResponse findDialogMessages(Integer from, Integer to) {
        return dialogClient.findDialogs(from, to);
    }

    public void sendMessage(Integer from, Integer to, String text) {
        dialogClient.sendMessage(new SendMessageRequest(from, to, text));
    }
}
