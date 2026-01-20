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
public class AddFriendsExecutor extends CommandExecutor {

    @Override
    public boolean validMessage(String message, TeamState state) {
        return Constants.PLUS_PATTERN.matcher(message).matches();
    }

    @Override
    public String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state) {
        if (state.getTeamConfig().isIgnoreAddition()) {
            return StringUtils.EMPTY;
        }

        if (!state.isCheckInStarted()) {
            return "Возможность изменения состава будет доступна после оповещения";
        }

        Long telegramId = user.getLeft();
        String username = user.getRight();
        String teamName = state.getTeamName();
        int number = Integer.parseInt(message.substring(1));

        Optional<FootballPlayer> currentPlayer = playerRepository.findByTelegramIdAndFootballTeamName(telegramId, teamName);

        if (!currentPlayer.isPresent()) {
            FootballPlayer newPlayer = FootballPlayer.builder()
                    .telegramId(telegramId)
                    .username(username)
                    .status(PlayerStatus.CALLED_FRIENDS)
                    .footballTeamName(teamName)
                    .calledFriends(number)
                    .timestamp(LocalDateTime.now())
                    .build();

            playerRepository.save(newPlayer);

            List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
            String total = playerCounterService.getTotal(players);

            return username + " сделал +" + number + ". Итого: " + total;
        }

        Integer previouslyCalled = currentPlayer.get().getCalledFriends();
        currentPlayer.get().setCalledFriends(previouslyCalled + number);
        playerRepository.save(currentPlayer.get());

        List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
        String total = playerCounterService.getTotal(players);

        return username + " сделал +" + number + ". Итого: " + total;
    }

}
