package be.technobel.chesstournament.pl.controllers;

import be.technobel.chesstournament.bll.exceptions.NotFoundException;
import be.technobel.chesstournament.bll.exceptions.TournamentRegistrationException;
import be.technobel.chesstournament.bll.exceptions.TournamentStartException;
import be.technobel.chesstournament.bll.exceptions.TournamentUnregistrationException;
import be.technobel.chesstournament.bll.services.TournamentService;
import be.technobel.chesstournament.bll.services.implementations.TournamentServiceImpl;
import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.repositories.TournamentRepository;
import be.technobel.chesstournament.pl.models.dtos.TournamentDto;
import be.technobel.chesstournament.pl.models.dtos.TournamentWithParticipantDto;
import be.technobel.chesstournament.pl.models.forms.TournamentForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
    private final TournamentService tournamentService;
    private final TournamentRepository tournamentRepository;


    public TournamentController(TournamentService tournamentService,
                                TournamentRepository tournamentRepository) {
        this.tournamentService = tournamentService;
        this.tournamentRepository = tournamentRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public void createTournament(@RequestBody TournamentForm form){
        tournamentService.createTournament(form);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public ResponseEntity<TournamentWithParticipantDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(TournamentWithParticipantDto.toDto(tournamentService.getById(id).orElseThrow(()-> new NotFoundException("Tournament not found."))));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/latest")
    public ResponseEntity<List<TournamentDto>> getLatestOpenTournaments() {
        List<TournamentDto> tournaments = tournamentService.getLatestOpenTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public ResponseEntity<String> registerForTournament(@RequestParam Long id, @RequestParam String username){
        try {
            tournamentService.registerForTournament(id, username);
            return ResponseEntity.ok("Player registered successfully for the tournament.");
        } catch (TournamentRegistrationException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unregister")
    public ResponseEntity<String> unregisterFromTournament(
            @RequestParam Long id,
            @RequestParam String username) {
        try {
            tournamentService.unregisterFromTournament(id, username);
            return ResponseEntity.ok("Player unregistered successfully from the tournament.");
        } catch (TournamentUnregistrationException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/start-tournament")
    public ResponseEntity<String> startTournament(@RequestParam Long id) {
        try {
            tournamentService.startTournament(id);
            return ResponseEntity.ok("Tournament started successfully.");
        } catch (TournamentStartException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/advance/{id}")
    public void advanceToNextRound(@PathVariable Long id) {
        tournamentService.advanceToNextRound(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/participants/scores/{id}")
    public List<UserEntity> getParticipantsWithScores(@PathVariable Long id) {
        return tournamentService.getParticipantsWithScores(id);
    }
}
