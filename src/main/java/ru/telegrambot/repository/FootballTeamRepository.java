package ru.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telegrambot.entity.FootballTeam;

import java.util.Optional;

public interface FootballTeamRepository extends JpaRepository<FootballTeam, Long> {

    Optional<FootballTeam> findByName(String name);

}
