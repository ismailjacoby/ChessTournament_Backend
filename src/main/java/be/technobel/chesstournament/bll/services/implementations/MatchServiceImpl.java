package be.technobel.chesstournament.bll.services.implementations;

import be.technobel.chesstournament.bll.exceptions.MatchModificationException;
import be.technobel.chesstournament.bll.exceptions.NotFoundException;
import be.technobel.chesstournament.bll.services.MatchService;
import be.technobel.chesstournament.bll.services.ScoreService;
import be.technobel.chesstournament.dal.models.entities.MatchEntity;
import be.technobel.chesstournament.dal.models.entities.ScoreEntity;
import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Result;
import be.technobel.chesstournament.dal.repositories.MatchRepository;
import be.technobel.chesstournament.dal.repositories.ScoreRepository;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final ScoreRepository scoreRepository;
    private final ScoreService scoreService;


    public MatchServiceImpl(MatchRepository matchRepository, ScoreRepository scoreRepository, ScoreService scoreService) {
        this.matchRepository = matchRepository;
        this.scoreRepository = scoreRepository;
        this.scoreService = scoreService;
    }

    /**
     * Modifies the result of a match with the specified ID.
     *
     * @param matchId the ID of the match to be modified.
     * @param result  the new result for the match.
     * @throws NotFoundException          if the match with the specified ID is not found.
     * @throws MatchModificationException if the match does not belong to the current round.
     */
    @Override
    public void modifyMatchResult(Long matchId, Result result) {
        // Retrieve the match
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match not found"));

        // Retrieve the associated tournament
        TournamentEntity tournament = match.getTournament();

        // Check if the match belongs to the current round
        if (match.getRound() != tournament.getCurrentRound()) {
            throw new MatchModificationException("You can only modify the result for the current round");
        }

        // Update the match result
        match.setResult(result);

        // Update the scores based on the match result
        scoreService.updateScores(match);

        // Save the updated match entity to the database
        matchRepository.save(match);
    }

    /**
     * Checks if all matches in the current round of the specified tournament have been played.
     *
     * @param tournament the tournament to check for played matches.
     * @return true if all matches in the current round are played, false otherwise.
     */
    private boolean areAllMatchesPlayed(TournamentEntity tournament) {
        int currentRound = tournament.getCurrentRound();

        for (MatchEntity match : tournament.getMatches()) {
            if (match.getRound() == currentRound && match.getResult() == Result.NOT_PLAYED) {
                // If there is at least one match in the current round that is not played, return false
                return false;
            }
        }
        // If all matches in the current round are played, return true
        return true;
    }


}
