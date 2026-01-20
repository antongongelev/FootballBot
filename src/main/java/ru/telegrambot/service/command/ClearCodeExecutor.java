package ru.telegrambot.service.command;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.telegrambot.util.TeamState;

@Service
public class ClearCodeExecutor extends CommandExecutor {

    @Override
    public boolean validMessage(String message, TeamState state) {
        String clearCode = state.getClearCode();
        long clearTimestamp = state.getClearTimestamp();
        long current = System.currentTimeMillis();
        return clearCode.equals(message) && (clearTimestamp < current && current < clearTimestamp + 60_000);
    }

    @Override
    public String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state) {
        playerRepository.deleteAllByFootballTeamName(state.getTeamName());
        state.setClearCode(StringUtils.EMPTY);
        state.setClearTimestamp(0L);
        return user.getRight() + " сбросил состав";
    }

}
