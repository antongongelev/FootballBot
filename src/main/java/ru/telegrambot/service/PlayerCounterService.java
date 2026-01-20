package ru.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.telegrambot.entity.FootballPlayer;
import ru.telegrambot.util.PlayerStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerCounterService {

    public String getTotal(List<FootballPlayer> players) {
        int doubt = getDoubt(players);
        int sure = getSure(players);

        if (doubt > 0) {
            return sure + " и ещё " + doubt + " под вопросом";
        }

        return String.valueOf(sure);
    }

    public int getSure(List<FootballPlayer> players) {
        return getSelf(players) + getFriends(players);
    }

    public int getFriends(List<FootballPlayer> players) {
        return players.stream()
                .map(FootballPlayer::getCalledFriends)
                .reduce(0, Integer::sum);
    }

    public int getSelf(List<FootballPlayer> players) {
        return (int) players.stream()
                .filter(p -> PlayerStatus.READY == p.getStatus())
                .count();
    }

    public int getDoubt(List<FootballPlayer> players) {
        return (int) players.stream()
                .filter(p -> PlayerStatus.DOES_NOT_KNOW == p.getStatus())
                .count();
    }

}
