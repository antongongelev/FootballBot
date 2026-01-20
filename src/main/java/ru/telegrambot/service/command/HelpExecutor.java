package ru.telegrambot.service.command;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.telegrambot.util.Constants;
import ru.telegrambot.util.TeamState;

@Service
public class HelpExecutor extends CommandExecutor {

    @Override
    public boolean validMessage(String message, TeamState state) {
        return Constants.HELP.equals(message);
    }

    @Override
    public String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state) {
        boolean ignoreInterrogation = state.getTeamConfig().isIgnoreInterrogation();
        boolean isIgnoreAddition = state.getTeamConfig().isIgnoreAddition();

        return "Основные команды:" + System.lineSeparator() +
                "'+' - Идешь" + System.lineSeparator() +
                "'-' - Сливаешься" + System.lineSeparator() +
                (ignoreInterrogation ? "" : "'?' - Под вопросом" + System.lineSeparator()) +
                (isIgnoreAddition ? "" : "'+n' - Плюсуешь n друзей (1-9)" + System.lineSeparator() + "'-n' - Минусуешь n друзей (1-9)" + System.lineSeparator()) +
                "'Состав' - Узнать состав" + System.lineSeparator() +
                "'Сброс' - Сброс состава";
    }

}
