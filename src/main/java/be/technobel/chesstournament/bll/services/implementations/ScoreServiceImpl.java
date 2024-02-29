package be.technobel.chesstournament.bll.services.implementations;

import be.technobel.chesstournament.bll.services.ScoreService;
import be.technobel.chesstournament.dal.models.entities.MatchEntity;
import be.technobel.chesstournament.dal.models.entities.ScoreEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Result;
import be.technobel.chesstournament.dal.repositories.ScoreRepository;
import be.technobel.chesstournament.pl.models.dtos.ScoreDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreServiceImpl(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }


    /**
     * Creates a new ScoreEntity for the specified player with initial values.
     *
     * @param player the player for whom to create the score.
     * @return the created ScoreEntity.
     */
    public ScoreEntity createScoreEntity(UserEntity player) {
        ScoreEntity score = new ScoreEntity();
        score.setPlayer(player);
        score.setMatchesPlayed(0);
        score.setVictories(0);
        score.setDefeats(0);
        score.setDraws(0);
        score.setScore(0.0);
        return score;
    }

    /**
     * Increments the number of victories for the specified player's score and updates the total score.
     *
     * @param score the score to be updated.
     */
    @Override
    public void incrementVictories(ScoreEntity score) {
        score.setVictories(score.getVictories() + 1);
        calculateTotalScore(score);
        scoreRepository.save(score);
    }

    /**
     * Increments the number of defeats for the specified player's score and updates the total score.
     *
     * @param score the score to be updated.
     */
    @Override
    public void incrementDefeats(ScoreEntity score) {
        score.setDefeats(score.getDefeats() + 1);
        calculateTotalScore(score);
        scoreRepository.save(score);
    }

    /**
     * Increments the number of draws for the specified player's score and updates the total score.
     *
     * @param score the score to be updated.
     */
    @Override
    public void incrementDraws(ScoreEntity score) {
        score.setDraws(score.getDraws() + 1);
        calculateTotalScore(score);
        scoreRepository.save(score);
    }

    /**
     * Increments the number of matches played for the specified player's score.
     *
     * @param score the score to be updated.
     */
    @Override
    public void incrementMatchesPlayed(ScoreEntity score) {
        score.setMatchesPlayed(score.getMatchesPlayed() + 1);
        scoreRepository.save(score);
    }

    /**
     * Calculates the total score for the specified player's score.
     *
     * @param score the score for which to calculate the total score.
     */
    @Override
    public void calculateTotalScore(ScoreEntity score) {
        double totalScore = score.getVictories() + 0.5 * score.getDraws();
        score.setScore(totalScore);
    }

    /**
     * Updates the scores for the players involved in the specified match based on the match result.
     *
     * @param match the match for which to update the scores.
     */
    @Override
    public void updateScores(MatchEntity match) {
        // Retrieve the players involved in the match
        UserEntity player1 = match.getPlayer1();
        UserEntity player2 = match.getPlayer2();

        // Fetch or create the score entities for the players
        ScoreEntity score1 = scoreRepository.findByPlayer(player1)
                .orElseGet(() -> createScoreEntity(player1));

        ScoreEntity score2 = scoreRepository.findByPlayer(player2)
                .orElseGet(() -> createScoreEntity(player2));

        // Update the scores based on the match result
        switch (match.getResult()) {
            case WHITE_WON:
                incrementVictories(score1);
                incrementDefeats(score2);
                break;
            case BLACK_WON:
                incrementDefeats(score1);
                incrementVictories(score2);
                break;
            case DRAW:
                incrementDraws(score1);
                incrementDraws(score2);
                break;
        }

        // Update the matches played
        incrementMatchesPlayed(score1);
        incrementMatchesPlayed(score2);

        // Calculate and update the total scores (you might have a specific scoring logic)
        calculateTotalScore(score1);
        calculateTotalScore(score2);

        // Save the changes to the scores
        scoreRepository.save(score1);
        scoreRepository.save(score2);
    }

    /**
     * Gets a list of ScoreDto objects representing player scores for a specific round in a tournament.
     *
     * @param tournamentId the ID of the tournament for which to fetch scores.
     * @return a list of ScoreDto objects.
     */
    @Override
    public List<ScoreDto> getScoresForRound(Long tournamentId) {
        // Fetch all scores for the given tournament
        List<ScoreEntity> allScoresForTournament = scoreRepository.findByMatch_Tournament_Id(tournamentId);

        // Sort the scores in descending order by score
        allScoresForTournament.sort((score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));

        // Convert ScoreEntity objects to ScoreDto for presentation
        return allScoresForTournament.stream()
                .map(ScoreDto::toDto)
                .collect(Collectors.toList());
    }
}
