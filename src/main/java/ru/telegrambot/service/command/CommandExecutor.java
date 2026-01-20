package ru.telegrambot.service.command;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telegrambot.repository.FootballPlayerRepository;
import ru.telegrambot.service.DayService;
import ru.telegrambot.service.PlayerCounterService;
import ru.telegrambot.service.PlayerTagService;
import ru.telegrambot.util.TeamState;

@Service
public abstract class CommandExecutor {

    @Autowired
    protected DayService dayService;

    @Autowired
    protected PlayerTagService playerTagService;

    @Autowired
    protected PlayerCounterService playerCounterService;

    @Autowired
    protected FootballPlayerRepository playerRepository;

    public abstract boolean validMessage(String message, TeamState state);

    public abstract String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state);

}
