package ru.telegrambot.service.command;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.FootballPlayer;
import ru.telegrambot.util.Constants;
import ru.telegrambot.util.PlayerStatus;
import ru.telegrambot.util.TeamState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AddMeExecutor extends CommandExecutor {

    @Override
    public boolean validMessage(String message, TeamState state) {
        return Constants.ADD_ME.equals(message);
    }

    @Override
    public String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state) {
        if (!state.isCheckInStarted()) {
            return "Возможность изменения состава будет доступна после оповещения";
        }

        Long telegramId = user.getLeft();
        String username = user.getRight();
        String teamName = state.getTeamName();

        Optional<Pair<String, Boolean>> jokeInfo = playerTagService.getJokeByID(telegramId);
        String joke = jokeInfo.map(Pair::getLeft).orElse(StringUtils.EMPTY);
        String dot = jokeInfo.map(Pair::getRight).orElse(true) ? "." : StringUtils.EMPTY;

        Optional<FootballPlayer> currentPlayer = playerRepository.findByTelegramIdAndFootballTeamName(telegramId, teamName);

        if (!currentPlayer.isPresent()) {
            FootballPlayer newPlayer = FootballPlayer.builder()
                    .telegramId(telegramId)
                    .username(username)
                    .status(PlayerStatus.READY)
                    .footballTeamName(teamName)
                    .timestamp(LocalDateTime.now())
                    .calledFriends(0)
                    .build();

            playerRepository.save(newPlayer);

            List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
            String total = playerCounterService.getTotal(players);

            return String.format("%s вписался%s%s Итого: %s", username, joke, dot, total);
        }

        PlayerStatus status = currentPlayer.get().getStatus();
        if (PlayerStatus.READY == status) {
            return username + " попытался вписаться, хотя уже был вписан";
        }

        currentPlayer.get().setStatus(PlayerStatus.READY);
        currentPlayer.get().setTimestamp(LocalDateTime.now());
        playerRepository.save(currentPlayer.get());

        List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
        String total = playerCounterService.getTotal(players);

        if (PlayerStatus.CALLED_FRIENDS == status) {
            return String.format("%s вписался%s%s Итого: %s", username, joke, dot, total);
        }

        return username + " поменял статус с '" + status.getValue() + "' на '" + PlayerStatus.READY.getValue() + "'. Итого: " + total;
    }

}
