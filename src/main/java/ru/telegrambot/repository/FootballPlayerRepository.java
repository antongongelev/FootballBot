package ru.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import ru.telegrambot.entity.FootballPlayer;

import java.util.List;
import java.util.Optional;

public interface FootballPlayerRepository extends JpaRepository<FootballPlayer, Long> {

    List<FootballPlayer> findAllByFootballTeamName(String teamName);

    Optional<FootballPlayer> findByTelegramIdAndFootballTeamName(Long telegramId, String teamName);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void deleteAllByFootballTeamName(String teamName);

}
