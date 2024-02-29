package be.technobel.chesstournament.bll.services.implementations;

import be.technobel.chesstournament.bll.exceptions.*;
import be.technobel.chesstournament.bll.mailing.EmailSenderService;
import be.technobel.chesstournament.bll.services.TournamentService;
import be.technobel.chesstournament.dal.models.entities.MatchEntity;
import be.technobel.chesstournament.dal.models.entities.ScoreEntity;
import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Gender;
import be.technobel.chesstournament.dal.models.enums.Result;
import be.technobel.chesstournament.dal.models.enums.Status;
import be.technobel.chesstournament.dal.models.enums.UserCategory;
import be.technobel.chesstournament.dal.repositories.MatchRepository;
import be.technobel.chesstournament.dal.repositories.ScoreRepository;
import be.technobel.chesstournament.dal.repositories.TournamentRepository;
import be.technobel.chesstournament.dal.repositories.UserRepository;
import be.technobel.chesstournament.pl.models.dtos.TournamentDto;
import be.technobel.chesstournament.pl.models.forms.TournamentForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final ScoreRepository scoreRepository;
    private final EmailSenderService emailSenderService;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, UserRepository userRepository, MatchRepository matchRepository, ScoreRepository scoreRepository, EmailSenderService emailSenderService) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
        this.scoreRepository = scoreRepository;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Creates a new chess tournament based on the provided form.
     *
     * @param form the TournamentForm containing the details of the new tournament.
     * @throws IllegalArgumentException if the provided form is null or if certain validation rules are not met.
     */
    @Override
    public void createTournament(TournamentForm form) {
        if(form==null){
            throw new IllegalArgumentException("Form can't be null");
        }

        // Rule: Minimum number of players must be less than or equal to the maximum number of players
        if (form.minPlayers() > form.maxPlayers()) {
            throw new IllegalArgumentException("Minimum number of players must be less than or equal to the maximum number of players");
        }
        // Rule: Minimum number of elo must be less than or equal to the maximum number of players
        if (form.minElo() > form.maxElo()) {
            throw new IllegalArgumentException("Minimum elo must be less than or equal to the maximum number of elo");
        }

        // Rule: Registration end date must be after the current date plus the minimum number of players
        Date today = new Date();
        Date registationEndDate = form.registrationEndDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, form.minPlayers());
        Date minimumRegistationEndDate = calendar.getTime();

        if(registationEndDate.before(minimumRegistationEndDate)){
            throw new IllegalArgumentException("Registration end date must be after the current date plus the minimum number of players");
        }

        TournamentEntity tournamentEntity = new TournamentEntity();
        tournamentEntity.setName(form.name());
        tournamentEntity.setLocation(form.location());

        int minPlayers = form.minPlayers();
        int maxPlayers = form.maxPlayers();
        int minElo = form.minElo();
        int maxElo = form.maxElo();

        // Validate minPlayers and maxPlayers
        if (minPlayers < 2) {
            throw new IllegalArgumentException("There must be at least 2 players in the tournament.");
        } else if (maxPlayers > 32) {
            throw new IllegalArgumentException("There must be less than 32 players in the tournament.");
        } else if (minPlayers >= maxPlayers) {
            throw new IllegalArgumentException("Min Players must be less than Max Players.");
        }

        // Validate minElo and maxElo
        if (minElo < 0 || maxElo < 0 || minElo > 3000 || maxElo > 3000) {
            throw new IllegalArgumentException("Elo values must be between 0 and 3000.");
        } else if (minElo >= maxElo) {
            throw new IllegalArgumentException("Min Elo must be less than Max Elo.");
        }
        tournamentEntity.setMinPlayers(minPlayers);
        tournamentEntity.setMaxPlayers(maxPlayers);
        tournamentEntity.setMinElo(minElo);
        tournamentEntity.setMaxElo(maxElo);
        tournamentEntity.setCategory(form.category());
        tournamentEntity.setCurrentRound(0); //Rule: Current round starts at 0
        tournamentEntity.setStatus(Status.WAITING_FOR_PLAYERS); // Rule: A newly created tournament will have the status "waiting for players"
        tournamentEntity.setWomenOnly(form.womenOnly());
        tournamentEntity.setCreationDate(today);//Rule: creation Date is today's date
        tournamentEntity.setUpdateDate(today);
        tournamentEntity.setRegistrationEndDate(form.registrationEndDate());
        tournamentEntity.setNbOfPlayersRegistered(0);

        tournamentRepository.save(tournamentEntity);

        emailSenderService.sendTournamentCreationEmail(tournamentEntity.getName(),tournamentEntity.getLocation(),tournamentEntity.getMinElo(),tournamentEntity.getMaxElo(),tournamentEntity.getMinPlayers(),tournamentEntity.getMaxPlayers(),tournamentEntity.isWomenOnly(),tournamentEntity.getRegistrationEndDate());
    }

    /**
     * Registers a user for a chess tournament.
     *
     * @param tournamentId the ID of the tournament for which to register the user.
     * @param username     the username of the user to register.
     * @throws TournamentRegistrationException if registration fails due to various reasons.
     */
    @Override
    public void registerForTournament(Long tournamentId, String username) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new NotFoundException("Tournament not found"));
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        // Check if the tournament has not started
        if (tournament.getStatus() != Status.WAITING_FOR_PLAYERS) {
            throw new TournamentRegistrationException("Tournament has already started");
        }

        // Check if the registration deadline is not passed
        if (tournament.getRegistrationEndDate().toInstant().isBefore(Instant.now())){
            throw new TournamentRegistrationException("Registration deadline has passed");
        }

        // Check if player is already registered
        if (tournament.getParticipants().contains(user)){
            throw new TournamentRegistrationException("Player is already registered");
        }

        // Check if the tournament has not reached the maximum number of participants
        if (tournament.getParticipants().size() >= tournament.getMaxPlayers()) {
            throw new TournamentRegistrationException("Tournament has reached maximum participants");
        }

        //Check if eligible based on elo rank
        if (user.getElo() < tournament.getMinElo() || user.getElo() > tournament.getMaxElo()) {
            throw new TournamentRegistrationException("Player is not eligible to register based on their elo");
        }

        // Calculate the age of the player at the end of the registration period
        int age = calculateAge(user.getDateOfBirth(), tournament.getRegistrationEndDate());

        // Check if the age of the player allows registration
        checkAgeEligibility(age, tournament.getCategory());

        // Check if the ELO of the player allows registration
        checkEloEligibility(user.getElo(), tournament.getMinElo(), tournament.getMaxElo());

        // Check if the gender of the player allows registration
        checkGenderEligibility(user.getGender(), tournament.isWomenOnly());

        // If all checks pass, add the player to the tournament
        tournament.getParticipants().add(user);

        tournamentRepository.save(tournament);
    }

    /**
     * Unregisters a user from a chess tournament.
     *
     * @param tournamentId the ID of the tournament from which to unregister the user.
     * @param username     the username of the user to unregister.
     * @throws TournamentUnregistrationException if unregistration fails due to various reasons.
     */
    @Override
    public void unregisterFromTournament(Long tournamentId, String username) {
        // Retrieve the tournament and user
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament not found"));

        // Retrieve the user
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if the tournament has not started
        if (tournament.getStatus() != Status.WAITING_FOR_PLAYERS) {
            throw new TournamentUnregistrationException("Cannot unregister, tournament has already started");
        }

        // Check if the player is registered
        UserEntity registeredUser = null;
        for (UserEntity participant: tournament.getParticipants()){
            if (participant.equals(user)){
                registeredUser = participant;
                break;
            }
        }

        if (registeredUser==null) {
            throw new TournamentUnregistrationException("Player is not registered for the tournament");
        }

        tournament.getParticipants().remove(registeredUser);
        tournamentRepository.save(tournament);
    }

    /**
     * Retrieves a tournament by its ID along with its participants.
     *
     * @param id the ID of the tournament to retrieve.
     * @return an Optional containing the TournamentEntity if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<TournamentEntity> getById(Long id) {
        return tournamentRepository.findByIdWithParticipants(id);
    }

    /**
     * Gets a list of the latest open chess tournaments.
     *
     * @return a list of TournamentDto objects representing the latest open tournaments.
     */
    @Override
    public List<TournamentDto> getLatestOpenTournaments() {
        List<Status> notCompleted = Arrays.asList(Status.IN_PROGRESS, Status.WAITING_FOR_PLAYERS);
        List<TournamentEntity> tournaments = tournamentRepository.findTop10ByStatusInOrderByUpdateDateDesc(notCompleted);

        return tournaments.stream()
                .map(TournamentDto::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a chess tournament based on its ID.
     *
     * @param tournamentId the ID of the tournament to delete.
     * @throws IllegalArgumentException if the tournament cannot be deleted (e.g., if it has already started).
     * @throws NoSuchElementException   if the tournament with the specified ID is not found.
     */
    public void deleteTournament(Long tournamentId){
        // Get tournament
        Optional<TournamentEntity> optionalTournament = tournamentRepository.findById(tournamentId);

        // Check if the tournament exists
        if (optionalTournament.isPresent()){
            TournamentEntity tournament = optionalTournament.get();
            Set<UserEntity> participants = tournament.getParticipants();
            emailSenderService.sendTournamentWithParticipantsCancelled(tournament.getName(),tournament.getLocation(),participants);
            // Check if the tournament hasn't started yet
            if (tournament.getStatus()==Status.WAITING_FOR_PLAYERS){
                tournament.getParticipants().clear();
                tournamentRepository.save(tournament);

                tournamentRepository.deleteById(tournamentId);

            } else {
                throw new IllegalArgumentException("Only tournaments that haven't started can be deleted.");
            }
        } else {
            throw new NoSuchElementException("Tournament not found with ID " + tournamentId + " not found.");
        }
    }

    /**
     * Starts a chess tournament by setting its current round and generating pairings for the participants.
     *
     * @param tournamentId the ID of the tournament to start.
     * @throws TournamentStartException if the tournament cannot be started (e.g., if the minimum number of participants is not reached).
     */
    @Override
    public void startTournament(Long tournamentId) {
        // Retrieve the tournament
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament not found"));


        // Check if the tournament has reached the minimum number of participants
        if (tournament.getParticipants().size() < tournament.getMinPlayers()) {
            throw new TournamentStartException("Cannot start tournament, minimum number of participants not reached");
        }

        // Check if the registration deadline is passed
        if (tournament.getRegistrationEndDate().toInstant().isAfter(Instant.now())) {
            throw new TournamentStartException("Cannot start tournament, registration deadline not passed");
        }

        // Set the current round to 1
        tournament.setCurrentRound(1);

        // Update the tournament's update date
        tournament.setUpdateDate(new Date());

        // Generate pairings for all players (Round Robin – Aller-Retour)
        List<UserEntity> participants = new ArrayList<>(tournament.getParticipants());
        generatePairings(tournament, participants);

        // Save the updated tournament
        tournamentRepository.save(tournament);
    }

    /**
     * Advances a chess tournament to the next round.
     *
     * @param tournamentId the ID of the tournament to advance.
     * @throws RoundAdvanceException if the tournament cannot advance to the next round (e.g., if not all matches are played).
     */
    @Override
    public void advanceToNextRound(Long tournamentId) {
        // Retrieve the tournament
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament not found"));

        // Check if all matches of the current round have been played
        if (!areAllMatchesPlayed(tournament)) {
            throw new RoundAdvanceException("Cannot advance to the next round until all matches are played");
        }

        // Increment the current round
        tournament.setCurrentRound(tournament.getCurrentRound() + 1);

        // Save the updated tournament entity to the database
        tournamentRepository.save(tournament);
    }

    /**
     * Gets a list of participants in a chess tournament along with their scores.
     *
     * @param tournamentId the ID of the tournament for which to retrieve participants with scores.
     * @return a list of UserEntity objects representing participants, sorted by score (descending).
     */
    @Override
    public List<UserEntity> getParticipantsWithScores(Long tournamentId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament not found"));

        // Fetch participants with scores and sort by score (descending)
        return tournament.getParticipants().stream()
                .sorted(Comparator.comparingDouble(this::getScoreForUser).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the score for a user based on the scoring logic.
     *
     * @param user the UserEntity for which to retrieve the score.
     * @return the score of the user.
     * @throws NotFoundException if the score is not found for the user.
     */
    private double getScoreForUser(UserEntity user) {
        // You might need to adjust this based on your scoring logic
        ScoreEntity userScore = scoreRepository.findByPlayer(user)
                .orElseThrow(() -> new NotFoundException("Score not found for user"));
        return userScore.getScore();
    }

    /**
     * Calculates the age of a player based on the date of birth and a reference date.
     *
     * @param dateOfBirth   the date of birth of the player.
     * @param referenceDate the reference date for age calculation.
     * @return the age of the player.
     */
    public int calculateAge(Date dateOfBirth, Date referenceDate) {
        LocalDate birthDate = dateOfBirth.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = referenceDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        return Period.between(birthDate, endDate).getYears();
    }

    /**
     * Checks the age eligibility of a player for a specific tournament category.
     *
     * @param age      the age of the player.
     * @param category the UserCategory of the tournament.
     * @throws TournamentRegistrationException if the age criteria are not met for the tournament category.
     */
    private void checkAgeEligibility(int age, UserCategory category) {
        switch (category) {
            case JUNIOR:
                if (age >= 18) {
                    throw new TournamentRegistrationException("Player is too old for the Junior category");
                }
                break;
            case SENIOR:
                if (age < 18 || age >= 60) {
                    throw new TournamentRegistrationException("Player does not meet age criteria for the Senior category");
                }
                break;
            case VETERAN:
                if (age < 60) {
                    throw new TournamentRegistrationException("Player is too young for the Veteran category");
                }
                break;
            // Handle other categories if needed
            default:
                throw new TournamentRegistrationException("Invalid tournament category");
        }
    }

    /**
     * Checks the ELO eligibility of a player for a specific tournament.
     *
     * @param playerElo the ELO rating of the player.
     * @param minElo    the minimum ELO required for the tournament.
     * @param maxElo    the maximum ELO allowed for the tournament.
     * @throws TournamentRegistrationException if the player's ELO does not meet the tournament criteria.
     */
    private void checkEloEligibility(int playerElo, Integer minElo, Integer maxElo) {
        if ((minElo != null && playerElo < minElo) || (maxElo != null && playerElo > maxElo)) {
            throw new TournamentRegistrationException("Player's ELO does not meet the tournament criteria");
        }
    }

    /**
     * Checks the gender eligibility of a player for a specific tournament.
     *
     * @param playerGender the gender of the player.
     * @param isWomenOnly  indicates if the tournament is restricted to female and other gender players.
     * @throws TournamentRegistrationException if the gender criteria are not met for the tournament.
     */
    private void checkGenderEligibility(Gender playerGender, boolean isWomenOnly) {
        if (isWomenOnly && playerGender != Gender.FEMALE && playerGender != Gender.OTHER) {
            throw new TournamentRegistrationException("Only female and other gender players are allowed for this tournament");
        }
    }

    /**
     * Generates pairings for a tournament's participants.
     *
     * @param tournament  the TournamentEntity for which to generate pairings.
     * @param participants the list of participants for the tournament.
     */
    private void generatePairings(TournamentEntity tournament, List<UserEntity> participants) {
        int numParticipants = participants.size();

        if (numParticipants % 2 != 0) {
            UserEntity dummyPlayer = new UserEntity();
            dummyPlayer.setUsername("JohnDoe");
            participants.add(dummyPlayer);
        }

        int totalRounds = numParticipants * 2 - 1;


        for (int round = 1; round < totalRounds; round++) {
            for (int i = 0; i < numParticipants / 2; i++) {
                UserEntity player1 = participants.get(i);
                UserEntity player2 = participants.get(numParticipants - 1 - i);

                // Create a match for the current round
                createMatch(tournament, round, player1, player2);
            }

            // Rotate participants
            rotateParticipants(participants);
        }
    }

    /**
     * Rotates the list of participants for the next round.
     *
     * @param participants the list of participants to rotate.
     */
    private void rotateParticipants(List<UserEntity> participants) {
        // Rotate the participants list for the next round
        // For example, if the list is [A, B, C, D], after rotation it becomes [D, A, B, C]
        UserEntity lastParticipant = participants.remove(participants.size() - 1);
        participants.add(1, lastParticipant);
    }

    /**
     * Creates a match for a specific round between two players in a tournament.
     *
     * @param tournament the TournamentEntity in which the match is played.
     * @param round      the round number of the match.
     * @param player1    the first player participating in the match.
     * @param player2    the second player participating in the match.
     */
    private void createMatch(TournamentEntity tournament, int round, UserEntity player1, UserEntity player2) {
        MatchEntity match = new MatchEntity();
        match.setTournament(tournament);
        match.setRound(round);
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        // Save the match entity to the database
        matchRepository.save(match);
    }


    /**
     * Checks if all matches in the current round of a tournament are played.
     *
     * @param tournament the TournamentEntity to check for played matches.
     * @return true if all matches are played, false otherwise.
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
