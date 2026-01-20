package ru.telegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.telegrambot.util.PlayerStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "football_player")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FootballPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long telegramId;

    private String username;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private PlayerStatus status;

    private Integer calledFriends;

    private String footballTeamName;

}
