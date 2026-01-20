package ru.telegrambot.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.telegrambot.configuration.PropertyStorage;

@Data
@RequiredArgsConstructor
public class TeamState {

    private long clearTimestamp = 0L;
    private boolean reportSent = false;
    private boolean checkInStarted = false;
    private String clearCode = StringUtils.EMPTY;

    private final PropertyStorage.TeamConfig teamConfig;

    public String getTeamName() {
        return teamConfig.getName();
    }

    public void clearState() {
        clearTimestamp = 0L;
        reportSent = false;
        checkInStarted = false;
        clearCode = StringUtils.EMPTY;
    }

}
