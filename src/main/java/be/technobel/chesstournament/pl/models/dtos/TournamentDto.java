package be.technobel.chesstournament.pl.models.dtos;

import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.enums.UserCategory;
import be.technobel.chesstournament.dal.models.enums.Status;

import java.util.Date;

public record TournamentDto(Long id,
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
                            Date registrationEndDate) {
    public static TournamentDto toDto(TournamentEntity tournament){
        return new TournamentDto(tournament.getId(),
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
                tournament.getRegistrationEndDate());
    }
}
