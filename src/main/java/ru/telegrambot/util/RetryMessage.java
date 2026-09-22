package ru.telegrambot.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Data
@RequiredArgsConstructor
public class RetryMessage {

    private int retries = 0;
    private boolean success = false;

    private final int maxReties = 30;
    private final SendMessage message;

}
