package be.technobel.chesstournament.bll.services;

import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Result;
import be.technobel.chesstournament.pl.models.dtos.TournamentDto;
import be.technobel.chesstournament.pl.models.forms.TournamentForm;

import java.util.List;
import java.util.Optional;

public interface TournamentService {
    void createTournament(TournamentForm form);
    void deleteTournament(Long tournamentId);
    Optional<TournamentEntity> getById(Long id);
    List<TournamentDto> getLatestOpenTournaments();
    void registerForTournament(Long tournamentId, String username);
    void unregisterFromTournament(Long tournamentId, String username);
    void startTournament(Long tournamentId);
    void advanceToNextRound(Long tournamentId);
    List<UserEntity> getParticipantsWithScores(Long tournamentId);
}
