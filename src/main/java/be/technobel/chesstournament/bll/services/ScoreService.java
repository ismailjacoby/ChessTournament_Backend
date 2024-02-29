package be.technobel.chesstournament.bll.services;

import be.technobel.chesstournament.dal.models.entities.MatchEntity;
import be.technobel.chesstournament.dal.models.entities.ScoreEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Result;
import be.technobel.chesstournament.pl.models.dtos.ScoreDto;

import java.util.List;

public interface ScoreService {

    ScoreEntity createScoreEntity(UserEntity player);

    void incrementVictories(ScoreEntity score);

    void incrementDefeats(ScoreEntity score);

    void incrementDraws(ScoreEntity score);

    void incrementMatchesPlayed(ScoreEntity score);

    void calculateTotalScore(ScoreEntity score);
    void updateScores(MatchEntity match);
    public List<ScoreDto> getScoresForRound(Long tournamentId);


}
