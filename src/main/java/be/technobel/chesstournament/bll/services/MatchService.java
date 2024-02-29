package be.technobel.chesstournament.bll.services;

import be.technobel.chesstournament.dal.models.enums.Result;

public interface MatchService {
    void modifyMatchResult(Long matchId, Result result);

}
