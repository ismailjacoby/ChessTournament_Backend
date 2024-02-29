package be.technobel.chesstournament.dal;

import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Gender;
import be.technobel.chesstournament.dal.models.enums.Role;
import be.technobel.chesstournament.dal.models.enums.Status;
import be.technobel.chesstournament.dal.models.enums.UserCategory;
import be.technobel.chesstournament.dal.repositories.TournamentRepository;
import be.technobel.chesstournament.dal.repositories.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Component
public class DataInit  implements InitializingBean {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TournamentRepository tournamentRepository;

    public DataInit(UserRepository userRepository, PasswordEncoder passwordEncoder,
                    TournamentRepository tournamentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tournamentRepository = tournamentRepository;
    }



    Random random = new Random();

    @Override
    public void afterPropertiesSet() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        UserEntity user = new UserEntity();
        user.setUsername("checkmate");
        user.setPassword(passwordEncoder.encode("Test1234="));
        user.setEmail("checkmatemonsieur@gmail.com");
        Date dateOfBirth = format.parse("1993-10-09");
        user.setDateOfBirth(dateOfBirth);
        user.setGender(Gender.MALE);
        user.setElo(2990);
        user.setRole(Role.ADMIN);
        user.setEnabled(true);

        userRepository.save(user);

        for (int i = 1; i <= 10; i++) {
            UserEntity player = new UserEntity();
            player.setUsername("player"+i);
            player.setPassword(passwordEncoder.encode("Test1234="));
            player.setEmail("player"+i+"@gmail.com");
            Date playerDateOfBirth = generateRandomBirthdate(random);
            player.setDateOfBirth(playerDateOfBirth);
            player.setGender(random.nextInt(2) == 0 ? Gender.MALE : Gender.FEMALE);
            player.setElo(random.nextInt(3000));
            player.setRole(Role.PLAYER);
            player.setEnabled(true);
            userRepository.save(player);
        }


        Random random = new Random();
        int randomGender = random.nextInt(2);
        int randomElo = random.nextInt(3000);
        Date datePast = format.parse("2023-12-01");
        Date datePastCreation = format.parse("2024-01-01");
        Date dateToday = format.parse("2024-10-09");
        Date dateFuture = format.parse("2024-10-09");

        // Tournament 1
        TournamentEntity tournament = new TournamentEntity();
        tournament.setName("World Cup Tournament");
        tournament.setLocation("Arlon");
        tournament.setMinElo(0);
        tournament.setMaxElo(3000);
        tournament.setMinPlayers(2);
        tournament.setMaxPlayers(32);
        tournament.setCategory(UserCategory.JUNIOR);
        tournament.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament.setWomenOnly(false);
        tournament.setCreationDate(datePast);
        tournament.setUpdateDate(datePast);
        tournament.setRegistrationEndDate(dateFuture);
        tournamentRepository.save(tournament);

        // Tournament 2
        TournamentEntity tournament2 = new TournamentEntity();
        tournament2.setName("Technobel Tournament");
        tournament2.setLocation("Arlon");
        tournament2.setMinElo(0);
        tournament2.setMaxElo(3000);
        tournament2.setMinPlayers(2);
        tournament2.setMaxPlayers(8);
        tournament2.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament2.setCategory(UserCategory.JUNIOR);
        tournament2.setWomenOnly(false);
        tournament2.setCreationDate(datePast);
        tournament2.setUpdateDate(datePastCreation);
        tournament2.setRegistrationEndDate(datePastCreation);
        tournamentRepository.save(tournament2);

        // Tournament 3
        TournamentEntity tournament3 = new TournamentEntity();
        tournament3.setName("Grand Masters Challenge");
        tournament3.setLocation("Paris");
        tournament3.setMinElo(2000);
        tournament3.setMaxElo(2950);
        tournament3.setMinPlayers(4);
        tournament3.setMaxPlayers(16);
        tournament3.setCategory(UserCategory.SENIOR);
        tournament3.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament3.setWomenOnly(false);
        tournament3.setCreationDate(dateToday);
        tournament3.setUpdateDate(dateToday);
        tournament3.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament3);

        // Tournament 4
        TournamentEntity tournament4 = new TournamentEntity();
        tournament4.setName("Summer Blitz");
        tournament4.setLocation("Barcelona");
        tournament4.setMinElo(1000);
        tournament4.setMaxElo(2800);
        tournament4.setMinPlayers(4);
        tournament4.setMaxPlayers(16);
        tournament4.setCategory(UserCategory.JUNIOR);
        tournament4.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament4.setWomenOnly(true);
        tournament4.setCreationDate(dateToday);
        tournament4.setUpdateDate(dateToday);
        tournament4.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament4);

        // Tournament 5
        TournamentEntity tournament5 = new TournamentEntity();
        tournament5.setName("Rapid Fire Championship");
        tournament5.setLocation("New York");
        tournament5.setMinElo(1200);
        tournament5.setMaxElo(3000);
        tournament5.setMinPlayers(4);
        tournament5.setMaxPlayers(16);
        tournament5.setCategory(UserCategory.SENIOR);
        tournament5.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament5.setWomenOnly(false);
        tournament5.setCreationDate(dateToday);
        tournament5.setUpdateDate(dateToday);
        tournament5.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament5);

        // Tournament 6
        TournamentEntity tournament6 = new TournamentEntity();
        tournament6.setName("Golden Age Invitational");
        tournament6.setLocation("London");
        tournament6.setMinElo(1500);
        tournament6.setMaxElo(3000);
        tournament6.setMinPlayers(4);
        tournament6.setMaxPlayers(16);
        tournament6.setCategory(UserCategory.JUNIOR);
        tournament6.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament6.setWomenOnly(true);
        tournament6.setCreationDate(dateToday);
        tournament6.setUpdateDate(dateToday);
        tournament6.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament6);

        // Tournament 7
        TournamentEntity tournament7 = new TournamentEntity();
        tournament7.setName("Epic Battle Royale");
        tournament7.setLocation("Berlin");
        tournament7.setMinElo(800);
        tournament7.setMaxElo(2400);
        tournament7.setMinPlayers(2);
        tournament7.setMaxPlayers(8);
        tournament7.setCategory(UserCategory.SENIOR);
        tournament7.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament7.setWomenOnly(false);
        tournament7.setCreationDate(dateToday);
        tournament7.setUpdateDate(dateToday);
        tournament7.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament7);

        // Tournament 8
        TournamentEntity tournament8 = new TournamentEntity();
        tournament8.setName("Winter Warzone");
        tournament8.setLocation("Tokyo");
        tournament8.setMinElo(1300);
        tournament8.setMaxElo(2800);
        tournament8.setMinPlayers(2);
        tournament8.setMaxPlayers(8);
        tournament8.setCategory(UserCategory.JUNIOR);
        tournament8.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament8.setWomenOnly(true);
        tournament8.setCreationDate(dateToday);
        tournament8.setUpdateDate(dateToday);
        tournament8.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament8);

        // Tournament 9
        TournamentEntity tournament9 = new TournamentEntity();
        tournament9.setName("Blitz Brawl");
        tournament9.setLocation("Sydney");
        tournament9.setMinElo(1100);
        tournament9.setMaxElo(2600);
        tournament9.setMinPlayers(4);
        tournament9.setMaxPlayers(16);
        tournament9.setCategory(UserCategory.SENIOR);
        tournament9.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament9.setWomenOnly(false);
        tournament9.setCreationDate(dateToday);
        tournament9.setUpdateDate(dateToday);
        tournament9.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament9);

        // Tournament 10
        TournamentEntity tournament10 = new TournamentEntity();
        tournament10.setName("Youth Showdown");
        tournament10.setLocation("Rio de Janeiro");
        tournament10.setMinElo(1600);
        tournament10.setMaxElo(3000);
        tournament10.setMinPlayers(4);
        tournament10.setMaxPlayers(16);
        tournament10.setCategory(UserCategory.JUNIOR);
        tournament10.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament10.setWomenOnly(true);
        tournament10.setCreationDate(dateToday);
        tournament10.setUpdateDate(dateToday);
        tournament10.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament10);

        // Tournament 11
        TournamentEntity tournament11 = new TournamentEntity();
        tournament11.setName("Chess Classic");
        tournament11.setLocation("Vienna");
        tournament11.setMinElo(1200);
        tournament11.setMaxElo(2800);
        tournament11.setMinPlayers(4);
        tournament11.setMaxPlayers(16);
        tournament11.setCategory(UserCategory.SENIOR);
        tournament11.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament11.setWomenOnly(false);
        tournament11.setCreationDate(dateToday);
        tournament11.setUpdateDate(dateToday);
        tournament11.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament11);

        // Tournament 12
        TournamentEntity tournament12 = new TournamentEntity();
        tournament12.setName("Queens' Gambit Open");
        tournament12.setLocation("Moscow");
        tournament12.setMinElo(1000);
        tournament12.setMaxElo(2500);
        tournament12.setMinPlayers(2);
        tournament12.setMaxPlayers(8);
        tournament12.setCategory(UserCategory.JUNIOR);
        tournament12.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament12.setWomenOnly(true);
        tournament12.setCreationDate(dateToday);
        tournament12.setUpdateDate(dateToday);
        tournament12.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament12);

        // Tournament 13
        TournamentEntity tournament13 = new TournamentEntity();
        tournament13.setName("Knight's Challenge");
        tournament13.setLocation("Amsterdam");
        tournament13.setMinElo(1500);
        tournament13.setMaxElo(3000);
        tournament13.setMinPlayers(4);
        tournament13.setMaxPlayers(16);
        tournament13.setCategory(UserCategory.SENIOR);
        tournament13.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament13.setWomenOnly(false);
        tournament13.setCreationDate(dateToday);
        tournament13.setUpdateDate(dateToday);
        tournament13.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament13);

        // Tournament 14
        TournamentEntity tournament14 = new TournamentEntity();
        tournament14.setName("Summer Sicilian");
        tournament14.setLocation("Sicily");
        tournament14.setMinElo(800);
        tournament14.setMaxElo(2400);
        tournament14.setMinPlayers(2);
        tournament14.setMaxPlayers(8);
        tournament14.setCategory(UserCategory.JUNIOR);
        tournament14.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament14.setWomenOnly(true);
        tournament14.setCreationDate(dateToday);
        tournament14.setUpdateDate(dateToday);
        tournament14.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament14);

        // Tournament 15
        TournamentEntity tournament15 = new TournamentEntity();
        tournament15.setName("Pawns Parade");
        tournament15.setLocation("Stockholm");
        tournament15.setMinElo(1100);
        tournament15.setMaxElo(2600);
        tournament15.setMinPlayers(4);
        tournament15.setMaxPlayers(16);
        tournament15.setCategory(UserCategory.SENIOR);
        tournament15.setStatus(Status.WAITING_FOR_PLAYERS);
        tournament15.setWomenOnly(false);
        tournament15.setCreationDate(dateToday);
        tournament15.setUpdateDate(dateToday);
        tournament15.setRegistrationEndDate(dateToday);
        tournamentRepository.save(tournament15);

        //addToTournament
        TournamentEntity t = tournamentRepository.findById(2L).orElseThrow(() -> new RuntimeException("Tournament not found"));

        UserEntity player1 = userRepository.findByUsername("player1")
                .orElseThrow(() -> new RuntimeException("Player1 not found"));
        UserEntity player2 = userRepository.findByUsername("player2")
                .orElseThrow(() -> new RuntimeException("Player2 not found"));
        UserEntity player3 = userRepository.findByUsername("player3")
                .orElseThrow(() -> new RuntimeException("Player3 not found"));
        UserEntity player4 = userRepository.findByUsername("player4")
                .orElseThrow(() -> new RuntimeException("Player4 not found"));

        tournament2.getParticipants().add(player1);
        player1.getTournaments().add(tournament2);

        tournament2.getParticipants().add(player2);
        player2.getTournaments().add(tournament2);

        tournament2.getParticipants().add(player3);
        player3.getTournaments().add(tournament2);

        tournament2.getParticipants().add(player4);
        player4.getTournaments().add(tournament2);

        userRepository.saveAll(Arrays.asList(player1, player2, player3, player4));
        tournamentRepository.save(tournament2);
    }

    private Date generateRandomBirthdate(Random random) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1950 + random.nextInt(74)); // Random year between 1950 and 2023
        calendar.set(Calendar.MONTH, random.nextInt(12));
        calendar.set(Calendar.DAY_OF_MONTH, random.nextInt(28) + 1);
        return calendar.getTime();
    }


}
