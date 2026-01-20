package ru.telegrambot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.telegrambot.configuration.PropertyStorage;
import ru.telegrambot.service.FootballService;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final PropertyStorage propertyStorage;
    private final FootballService footballService;

    @PostConstruct
    private void initialize() throws TelegramApiException {
        footballService.setTelegramBot(this);
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            footballService.onUpdate(update);
        } catch (Exception e) {
            log.error("Error during onUpdateReceived", e);
        }
    }

    @Override
    public String getBotUsername() {
        return propertyStorage.getName();
    }

    @Override
    public String getBotToken() {
        return propertyStorage.getToken();
    }

}
