package be.technobel.chesstournament.pl.models.dtos;

import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.enums.Status;
import be.technobel.chesstournament.dal.models.enums.UserCategory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public record TournamentWithParticipantDto(
        Long id,
        String name,
        String location,
        int nbOfRegisteredPlayers,
        int minPlayers,
        int maxPlayers,
        Integer minElo,
        Integer maxElo,
        UserCategory category,
        Status status,
        int currentRound,
        Date registrationEndDate,
        List<UserDto> participants
) {
    public static TournamentWithParticipantDto toDto(TournamentEntity tournament) {
        List<UserDto> participantsDto = tournament.getParticipants().stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());

        return new TournamentWithParticipantDto(tournament.getId(),
                tournament.getName(),
                tournament.getLocation(),
                tournament.getParticipants().size(),
                tournament.getMinPlayers(),
                tournament.getMaxPlayers(),
                tournament.getMinElo(),
                tournament.getMaxElo(),
                tournament.getCategory(),
                tournament.getStatus(),
                tournament.getCurrentRound(),
                tournament.getRegistrationEndDate(),
                participantsDto
        );
    }
}
