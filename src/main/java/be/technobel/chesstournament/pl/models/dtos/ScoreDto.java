package be.technobel.chesstournament.pl.models.dtos;

import be.technobel.chesstournament.dal.models.entities.ScoreEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;

public record ScoreDto(UserEntity player,
                       int matchesPlayed,
                       int victories,
                       int defeats,
                       int draws,
                       double score) {

    public static ScoreDto toDto(ScoreEntity score){
        return new ScoreDto(score.getPlayer(),
                score.getMatchesPlayed(),
                score.getVictories(),
                score.getDefeats(),
                score.getDraws(),
                score.getScore());
    }
}
