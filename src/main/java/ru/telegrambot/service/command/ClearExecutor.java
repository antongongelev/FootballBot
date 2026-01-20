package ru.telegrambot.service.command;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.telegrambot.util.Constants;
import ru.telegrambot.util.TeamState;

import java.util.Random;

@Service
public class ClearExecutor extends CommandExecutor {

    private static final Random RANDOM = new Random();

    @Override
    public boolean validMessage(String message, TeamState state) {
        return Constants.CLEAR.equals(message);
    }

    @Override
    public String executeAndGetMessage(Pair<Long, String> user, String message, TeamState state) {
        if (!state.isCheckInStarted()) {
            return "Возможность изменения состава будет доступна после оповещения";
        }

        String clearCode = generateCode();
        long clearTimestamp = System.currentTimeMillis();

        state.setClearCode(clearCode);
        state.setClearTimestamp(clearTimestamp);

        return "Для сброса состава отправьте код '" + clearCode + "' в течение минуты";
    }

    private String generateCode() {
        StrBuilder builder = new StrBuilder();

        for (int i = 0; i < Constants.CODE_LENGTH; i++) {
            builder.append(RANDOM.nextInt(10));
        }

        return builder.toString();
    }

}
