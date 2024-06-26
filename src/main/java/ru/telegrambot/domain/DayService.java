package ru.telegrambot.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Service
public class DayService {

    @Value("${domain.football.day}")
    private String FOOTBALL_DAY;

    @Value("${domain.football.check-in-before-hours}")
    private String CHECK_IN_BEFORE;

    @Value("${domain.football.send-team-report-before-hours}")
    private String SEND_TEAM_REPORT_BEFORE;

    private String footballDay;
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM H:mm", new Locale("ru"));

    @PostConstruct
    private void setup() throws Exception {
        footballDay = getNearest();
    }

    public void validateDay() throws Exception {
        String oldDay = footballDay;
        String nearest = getNearest();
        if (!footballDay.equals(nearest)) {
            footballDay = getNearest();
            throw new TeamException("Состав на " + oldDay + " был сброшен. Следующий футбол будет " + nearest);
        }
    }

    public boolean updateDay(String footballDay) throws Exception {
        boolean isFootballDayValid = Arrays.stream(footballDay.split(",")).map(String::trim).allMatch(i -> {
            try {
                String day = i.substring(0, i.indexOf('-'));
                String hour = i.substring(i.indexOf('-') + 1, i.indexOf(':'));
                String minute = i.substring(i.indexOf(':') + 1);

                boolean isDayValid = getDayOfWeek(day) != null;
                boolean isHourValid = 0 <= Integer.parseInt(hour) && Integer.parseInt(hour) <= 23;
                boolean isMinuteValid = 0 <= Integer.parseInt(minute) && Integer.parseInt(minute) <= 59;

                return isDayValid && isHourValid && isMinuteValid;
            } catch (Exception e) {
                return false;
            }
        });

        if (!isFootballDayValid) {
            return false;
        }

        FOOTBALL_DAY = footballDay;
        this.footballDay = getNearest();
        return true;
    }

    public boolean isTimeToCheckIn() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        return getNearestDate().getDate().minusHours(Long.parseLong(CHECK_IN_BEFORE)).isBefore(now);
    }

    public boolean isTimeToSendTeamReport() throws Exception {
        return getNearestDate().getDate().minusHours(Long.parseLong(SEND_TEAM_REPORT_BEFORE)).isBefore(LocalDateTime.now());
    }

    private FootballDate getNearestDate() throws Exception {

        Optional<FootballDate> nearest = Arrays.stream(FOOTBALL_DAY.split(",")).map(String::trim).map(i -> {
            try {
                LocalDateTime now = LocalDateTime.now();

                String day = i.substring(0, i.indexOf('-'));
                String hour = i.substring(i.indexOf('-') + 1, i.indexOf(':'));
                String minute = i.substring(i.indexOf(':') + 1);

                if (now.getDayOfWeek() == getDayOfWeek(day) &&
                        (now.getHour() < Integer.parseInt(hour) || now.getHour() == Integer.parseInt(hour) && now.getMinute() < Integer.parseInt(minute))) {

                    LocalDateTime date = now.withHour(Integer.parseInt(hour)).withMinute(Integer.parseInt(minute));
                    return new FootballDate(i.toUpperCase(), date);
                }

                LocalDateTime date = now.with(TemporalAdjusters.next(getDayOfWeek(day))).withHour(Integer.parseInt(hour)).withMinute(Integer.parseInt(minute));
                return new FootballDate(i.toUpperCase(), date);

            } catch (Exception e) {
                throw new RuntimeException("Неверно заданы дни футбола");
            }

        }).min((o1, o2) -> {
            if (o1.getDate().isEqual(o2.getDate())) {
                return 0;
            }
            if (o1.getDate().isBefore(o2.getDate())) {
                return -1;
            }
            return 1;
        });

        if (!nearest.isPresent()) {
            throw new Exception("Неверно заданы дни футбола");
        }

        return nearest.get();
    }

    private String getNearest() throws Exception {
        return getNearestDate().getDate().format(FORMATTER);
    }

    public String getDay() {
        return "*" + footballDay + "*";
    }

    private DayOfWeek getDayOfWeek(String day) throws IllegalAccessException {
        if (StringUtils.isEmpty(day)) {
            throw new IllegalAccessException("Неверный день для футбола");
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
                throw new IllegalAccessException("Неверный день для футбола");
        }
    }
}
