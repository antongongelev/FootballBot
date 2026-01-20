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
public class DoNotKnowExecutor extends CommandExecutor {

    @Override
    public boolean validMessage(String message, TeamState state) {
        return Constants.DO_NOT_KNOW.equals(message);
    }

    @Override
    public String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state) {
        if (state.getTeamConfig().isIgnoreInterrogation()) {
            return StringUtils.EMPTY;
        }

        if (!state.isCheckInStarted()) {
            return "Возможность изменения состава будет доступна после оповещения";
        }

        Long telegramId = user.getLeft();
        String username = user.getRight();
        String teamName = state.getTeamName();

        Optional<FootballPlayer> currentPlayer = playerRepository.findByTelegramIdAndFootballTeamName(telegramId, teamName);

        if (!currentPlayer.isPresent()) {
            FootballPlayer newPlayer = FootballPlayer.builder()
                    .telegramId(telegramId)
                    .username(username)
                    .status(PlayerStatus.DOES_NOT_KNOW)
                    .footballTeamName(teamName)
                    .timestamp(LocalDateTime.now())
                    .calledFriends(0)
                    .build();

            playerRepository.save(newPlayer);

            List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
            String total = playerCounterService.getTotal(players);

            return username + " под вопросом. Итого: " + total;
        }

        PlayerStatus status = currentPlayer.get().getStatus();
        if (PlayerStatus.DOES_NOT_KNOW == status) {
            return username + " усомнился что придет, хотя и так не был уверен";
        }

        currentPlayer.get().setStatus(PlayerStatus.DOES_NOT_KNOW);
        playerRepository.save(currentPlayer.get());

        List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
        String total = playerCounterService.getTotal(players);

        if (PlayerStatus.CALLED_FRIENDS == status) {
            return username + " под вопросом. Итого: " + total;
        }

        return username + " поменял статус с '" + status.getValue() + "' на '" + PlayerStatus.DOES_NOT_KNOW.getValue() + "'. Итого: " + total;
    }

}
