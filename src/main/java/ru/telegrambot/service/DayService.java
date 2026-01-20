package ru.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.telegrambot.configuration.PropertyStorage;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DayService {

    private final TeamStateStorage stateStorage;
    private final Map<String, Pair<LocalDateTime, String>> nearestDays = new HashMap<>();
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM H:mm", new Locale("ru"));

    @PostConstruct
    private void initialize() {
        stateStorage.getTeamStates().values().forEach(teamState -> {
            String teamName = teamState.getTeamName();
            String dates = teamState.getTeamConfig().getDay();
            LocalDateTime nearestDate = getNearestDate(dates);
            String formattedNearestDate = nearestDate.format(FORMATTER);
            nearestDays.put(teamName, Pair.of(nearestDate, formattedNearestDate));
        });
    }

    public boolean isFootballDayChanged(String teamName) {
        String footballDay = getFootballDay(teamName);
        String dates = stateStorage.getState(teamName).getTeamConfig().getDay();
        LocalDateTime nearestDate = getNearestDate(dates);
        String formattedNearestDate = nearestDate.format(FORMATTER);

        if (footballDay.equals(formattedNearestDate)) {
            return false;
        }

        nearestDays.put(teamName, Pair.of(nearestDate, formattedNearestDate));
        return true;
    }

    private LocalDateTime getNearestDate(String dates) {

        Optional<LocalDateTime> nearest = Arrays.stream(dates.split(";")).map(String::trim).map(i -> {
            try {
                LocalDateTime now = LocalDateTime.now();

                String day = i.substring(0, i.indexOf('_'));
                String hour = i.substring(i.indexOf('_') + 1, i.indexOf(':'));
                String minute = i.substring(i.indexOf(':') + 1);

                if (now.getDayOfWeek() == getDayOfWeek(day) &&
                        (now.getHour() < Integer.parseInt(hour) || now.getHour() == Integer.parseInt(hour) && now.getMinute() < Integer.parseInt(minute))) {
                    return now.withHour(Integer.parseInt(hour)).withMinute(Integer.parseInt(minute));
                }
                return now.with(TemporalAdjusters.next(getDayOfWeek(day))).withHour(Integer.parseInt(hour)).withMinute(Integer.parseInt(minute));

            } catch (Exception e) {
                log.error("Error during dates parsing", e);
                throw new IllegalArgumentException("Неверно заданы дни футбола");
            }

        }).min((o1, o2) -> {
            if (o1.isEqual(o2)) {
                return 0;
            }
            if (o1.isBefore(o2)) {
                return -1;
            }
            return 1;
        });

        if (!nearest.isPresent()) {
            throw new IllegalArgumentException("Неверно заданы дни футбола");
        }

        return nearest.get();
    }

    private DayOfWeek getDayOfWeek(String day) {
        if (StringUtils.isEmpty(day)) {
            throw new IllegalArgumentException("Неверный день для футбола");
        }
        switch (day.toUpperCase()) {
            case "ПОНЕДЕЛЬНИК":
                return DayOfWeek.MONDAY;
            case "ВТОРНИК":
                return DayOfWeek.TUESDAY;
            case "СРЕДА":
                return DayOfWeek.WEDNESDAY;
            case "ЧЕТВЕРГ":
                return DayOfWeek.THURSDAY;
            case "ПЯТНИЦА":
                return DayOfWeek.FRIDAY;
            case "СУББОТА":
                return DayOfWeek.SATURDAY;
            case "ВОСКРЕСЕНЬЕ":
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Неверный день для футбола");
        }
    }

    public boolean isTimeToCheckIn(String teamName) {
        LocalDateTime footballDate = getFootballDate(teamName);
        PropertyStorage.TeamConfig teamConfig = stateStorage.getState(teamName).getTeamConfig();
        Integer checkInBefore = teamConfig.getCheckInBeforeHours();
        return footballDate.minusHours(checkInBefore).isBefore(LocalDateTime.now());
    }

    public boolean isTimeToSendTeamReport(String teamName) {
        LocalDateTime footballDate = getFootballDate(teamName);
        PropertyStorage.TeamConfig teamConfig = stateStorage.getState(teamName).getTeamConfig();
        Integer sendReportBefore = teamConfig.getSendTeamReportBeforeHours();
        return footballDate.minusHours(sendReportBefore).isBefore(LocalDateTime.now());
    }

    public String getFootballDay(String teamName) {
        return nearestDays.get(teamName).getRight();
    }

    private LocalDateTime getFootballDate(String teamName) {
        return nearestDays.get(teamName).getLeft();
    }

}
