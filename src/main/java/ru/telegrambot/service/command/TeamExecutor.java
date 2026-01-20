package ru.telegrambot.service.command;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.FootballPlayer;
import ru.telegrambot.util.Constants;
import ru.telegrambot.util.PlayerStatus;
import ru.telegrambot.util.TeamState;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamExecutor extends CommandExecutor {

    @Override
    public boolean validMessage(String message, TeamState state) {
        return Constants.TEAM.equals(message);
    }

    @Override
    public String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state) {
        return executeAndGetMessage(state);
    }

    public String executeAndGetMessage(TeamState state) {
        String teamName = state.getTeamName();
        List<FootballPlayer> players = playerRepository.findAllByFootballTeamName(teamName);
        String footballDay = dayService.getFootballDay(teamName);
        String total = playerCounterService.getTotal(players);
        String team = getTeam(players);

        return "Состав на " + footballDay + ": " + System.lineSeparator()
                + Constants.DELIMITER + System.lineSeparator()
                + team
                + Constants.DELIMITER + System.lineSeparator()
                + "Итого: " + total;
    }

    private String getTeam(List<FootballPlayer> players) {
        StringBuilder builder = new StringBuilder();
        List<FootballPlayer> relevantPlayers = players
                .stream()
                .filter(this::isRelevantPlayer)
                .sorted(Comparator.comparing(FootballPlayer::getTimestamp))
                .collect(Collectors.toList());

        for (int i = 0; i < relevantPlayers.size(); i++) {
            builder.append(getPlayerReport(relevantPlayers.get(i), i));
        }

        if (StringUtils.isEmpty(builder.toString())) {
            builder.append("Пока никого...").append(System.lineSeparator());
        }

        return builder.toString();
    }

    private boolean isRelevantPlayer(FootballPlayer player) {
        boolean relevantStatus = PlayerStatus.READY == player.getStatus() || PlayerStatus.DOES_NOT_KNOW == player.getStatus();
        boolean calledFriends = player.getCalledFriends() > 0;

        return relevantStatus || calledFriends;
    }

    private String getPlayerReport(FootballPlayer player, int i) {
        int calledFriends = player.getCalledFriends();
        String username = player.getUsername();
        String status = player.getStatus().getValue();

        return ++i +
                ". " +
                username +
                " -> " +
                status +
                (calledFriends > 0 ? ". Позвал +" + calledFriends : "") +
                System.lineSeparator();
    }

}
