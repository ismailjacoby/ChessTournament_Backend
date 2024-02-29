package be.technobel.chesstournament.bll;

import be.technobel.chesstournament.bll.mailing.EmailSenderService;
import be.technobel.chesstournament.bll.services.implementations.TournamentServiceImpl;
import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.enums.Status;
import be.technobel.chesstournament.dal.models.enums.UserCategory;
import be.technobel.chesstournament.dal.repositories.TournamentRepository;
import be.technobel.chesstournament.dal.repositories.UserRepository;
import be.technobel.chesstournament.pl.models.dtos.TournamentDto;
import be.technobel.chesstournament.pl.models.forms.TournamentForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TournamentServiceTest {
    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    /*
        2. Create Tournament Test
     */

    @Test
    void createTournament_ValidForm_CreatesTournamentAndSendsEmail() {

        // Arrange
        Instant now = Instant.now();
        Instant registrationEndDate = now.plus(Duration.ofDays(5));
        TournamentForm form = new TournamentForm(
                "Chess Tournament",
                "Location",
                2,
                16,
                1200,
                2000,
                UserCategory.JUNIOR,
                Status.WAITING_FOR_PLAYERS,
                0,
                false,
                Date.from(registrationEndDate) // 1 day in the future
        );

        when(tournamentRepository.save(any())).thenReturn(new TournamentEntity());

        // Act
        tournamentService.createTournament(form);

        // Assert
        verify(tournamentRepository, times(1)).save(any());
        verify(emailSenderService, times(1)).sendTournamentCreationEmail(any(), any(), anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean(), any(Date.class));
    }

    @Test
    void createTournament_InvalidForm_Null_ThrowsIllegalArgumentException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> tournamentService.createTournament(null));
    }

    @Test
    void createTournament_MinPlayersGreaterThanMaxPlayers_ThrowsIllegalArgumentException() {
        // Arrange
        Instant now = Instant.now();
        Instant registrationEndDate = now.plus(Duration.ofDays(5));
        TournamentForm form = new TournamentForm(
                "Chess Tournament",
                "Location",
                16, 8,  // Min players greater than max players
                1200,
                2000,
                UserCategory.JUNIOR,
                Status.WAITING_FOR_PLAYERS,
                0,
                false,
                Date.from(registrationEndDate)
        );

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> tournamentService.createTournament(form));
    }

    @Test
    void createTournament_MinEloGreaterThanMaxElo_ThrowsIllegalArgumentException() {
        // Arrange
        Instant now = Instant.now();
        Instant registrationEndDate = now.plus(Duration.ofDays(5));
        TournamentForm form = new TournamentForm(
                "Chess Tournament",
                "Location",
                8,
                16,
                2000, 1200,  // Min elo greater than max elo
                UserCategory.JUNIOR,
                Status.WAITING_FOR_PLAYERS,
                0,
                false,
                Date.from(registrationEndDate)
        );

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> tournamentService.createTournament(form));
    }

    @Test
    void createTournament_RegistrationEndDateBeforeMinimumDate_ThrowsIllegalArgumentException() {
        // Arrange
        Instant now = Instant.now();
        Instant registrationEndDate = now.plus(Duration.ofDays(5));
        TournamentForm form = new TournamentForm(
                "Chess Tournament",
                "Location",
                8,
                16,
                1200,
                2000,
                UserCategory.JUNIOR,
                Status.WAITING_FOR_PLAYERS,
                0,
                false,
                Date.from(registrationEndDate) // 1 in the future
        );

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> tournamentService.createTournament(form));
    }

     /*
        3. Delete Tournament Test
     */

    @Test
    public void deleteTournament_InvalidTournamentId_ThrowException() {
        // Mock repository behavior
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Invoke the deleteTournament method with an invalid tournament ID and expect a NoSuchElementException
        assertThrows(NoSuchElementException.class, () -> tournamentService.deleteTournament(1L));
    }

    @Test
    public void deleteTournament_InProgressTournament_ExceptionThrown() {
        // Create an in-progress tournament
        TournamentEntity inProgressTournament = createInProgressTournament();
        Long tournamentId = inProgressTournament.getId();

        // Mock repository behavior
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(inProgressTournament));

        // Invoke the deleteTournament method and expect an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> tournamentService.deleteTournament(tournamentId));

        // Verify that deleteById was not called
        verify(tournamentRepository, never()).deleteById(tournamentId);

        // Verify that sendTournamentCancelled was not called
        verify(emailSenderService, never()).sendTournamentCancelled(any(), any());
    }

     /*
        7. Register for a Tournament (Tests)
     */

    // Test when there are no open tournaments
    @Test
    void getLatestOpenTournaments_NoOpenTournaments_ReturnsEmptyList() {

        // Act
        List<TournamentDto> result = tournamentService.getLatestOpenTournaments();

        // Assert
        assertEquals(0, result.size());
    }

    /*
        Helper Methods
     */

    // Helper method to create an open tournament
    private TournamentEntity createOpenTournament(String name) {
        TournamentEntity tournament = new TournamentEntity();
        tournament.setName(name);
        tournament.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament.setUpdateDate(new Date());
        return tournament;
    }


    // Create
    private TournamentEntity createWaitingTournament() {
        TournamentEntity waitingTournament = new TournamentEntity();
        waitingTournament.setId(1L);
        waitingTournament.setStatus(Status.WAITING_FOR_PLAYERS);
        return waitingTournament;
    }

    // Utility method to create an in-progress tournament
    private TournamentEntity createInProgressTournament() {
        TournamentEntity inProgressTournament = new TournamentEntity();
        inProgressTournament.setId(2L);
        inProgressTournament.setStatus(Status.IN_PROGRESS);
        return inProgressTournament;
    }

    // Utility method to create a completed tournament
    private TournamentEntity createCompletedTournament() {
        TournamentEntity completedTournament = new TournamentEntity();
        completedTournament.setId(3L);
        completedTournament.setStatus(Status.COMPLETED);
        return completedTournament;
    }



}