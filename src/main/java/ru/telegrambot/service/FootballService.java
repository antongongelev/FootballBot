package ru.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.telegrambot.bot.TelegramBot;
import ru.telegrambot.entity.FootballPlayer;
import ru.telegrambot.repository.FootballPlayerRepository;
import ru.telegrambot.service.command.CommandExecutor;
import ru.telegrambot.service.command.TeamExecutor;
import ru.telegrambot.util.TeamState;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FootballService {

    @Setter
    private TelegramBot telegramBot;

    private final DayService dayService;
    private final UserService userService;
    private final TeamExecutor teamExecutor;
    private final FootballPlayerRepository playerRepository;
    private final List<CommandExecutor> commandExecutors;
    private final TeamStateStorage stateStorage;

    public void onUpdate(Update update) {
        Message message = update.getMessage();
        if (Objects.isNull(message) || !message.hasText()) {
            return;
        }

        Long chatId = message.getChatId();
        String formattedText = message.getText().replaceAll(" ", StringUtils.EMPTY).toUpperCase();

        Optional<TeamState> teamState = stateStorage.getTeamStateByChatId(chatId);
        if (!teamState.isPresent()) {
            sendMessage("Этот бот не предназначен для данного чата", chatId.toString());
            return;
        }

        Optional<CommandExecutor> executor = commandExecutors.stream()
                .filter(exec -> exec.validMessage(formattedText, teamState.get()))
                .findFirst();

        if (executor.isPresent()) {
            Pair<Long, String> user = userService.getUserFromMessage(message);
            String executionMessage = executor.get().executeAndGetMessage(user, formattedText, teamState.get());
            sendMessage(executionMessage, String.valueOf(chatId));
        }
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void scheduledTask() {
        stateStorage.getTeamStates().forEach((teamName, teamState) -> {

            try {
                String chatId = teamState.getTeamConfig().getChatId();
                String oldFootballDay = dayService.getFootballDay(teamName);

                if (dayService.isFootballDayChanged(teamName)) {
                    String newFootballDay = dayService.getFootballDay(teamName);
                    playerRepository.deleteAllByFootballTeamName(teamName);
                    teamState.clearState();
                    sendMessage("Состав на " + oldFootballDay + " был сброшен. Следующий футбол будет " + newFootballDay, chatId);
                }

                String footballDay = dayService.getFootballDay(teamName);
                boolean timeToCheckIn = dayService.isTimeToCheckIn(teamName);
                boolean timeToSendReport = dayService.isTimeToSendTeamReport(teamName);
                boolean checkInStarted = teamState.isCheckInStarted();
                boolean reportSent = teamState.isReportSent();

                if (!checkInStarted && timeToCheckIn) {
                    teamState.setCheckInStarted(true);
                    List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
                    if (CollectionUtils.isEmpty(players)) {
                        sendMessage("Набираем состав на " + footballDay, chatId);
                    }
                }

                if (!reportSent && timeToSendReport) {
                    String teamMessage = teamExecutor.executeAndGetMessage(teamState);
                    sendMessage("Футбол скоро начнется...", chatId);
                    sendMessage(teamMessage, chatId);
                    teamState.setReportSent(true);
                }

            } catch (Exception e) {
                log.error("Error during scheduledTask for team '{}'", teamName, e);
            }

        });
    }

    private void sendMessage(String message, String chatId) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Can't send telegram message", e);
        }
    }

}
