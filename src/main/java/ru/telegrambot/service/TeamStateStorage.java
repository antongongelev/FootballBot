package ru.telegrambot.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.telegrambot.configuration.PropertyStorage;
import ru.telegrambot.entity.FootballTeam;
import ru.telegrambot.repository.FootballTeamRepository;
import ru.telegrambot.util.TeamState;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TeamStateStorage {

    private final PropertyStorage propertyStorage;
    private final FootballTeamRepository teamRepository;

    @Getter
    private final Map<String, TeamState> teamStates = new HashMap<>();

    @PostConstruct
    private void initialize() {
        propertyStorage.getTeams().forEach(teamConfig -> {
            String teamName = teamConfig.getName();
            teamStates.put(teamName, new TeamState(teamConfig));
            if (!teamRepository.findByName(teamName).isPresent()) {
                FootballTeam footballTeam = FootballTeam.builder().name(teamConfig.getName()).build();
                teamRepository.save(footballTeam);
            }
        });
    }

    public TeamState getState(String teamName) {
        return teamStates.get(teamName);
    }

    public Optional<TeamState> getTeamStateByChatId(Long chatId) {
        return teamStates.values()
                .stream()
                .filter(teamState -> teamState.getTeamConfig().getChatId().equals(String.valueOf(chatId)))
                .map(teamState -> teamStates.get(teamState.getTeamName()))
                .findFirst();
    }

}
