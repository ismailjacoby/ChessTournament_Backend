package be.technobel.chesstournament.pl.controllers;

import be.technobel.chesstournament.bll.services.ScoreService;
import be.technobel.chesstournament.pl.models.dtos.ScoreDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/score")
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ScoreDto>> getScoresForRound(@RequestParam Long tournamentId){
        List<ScoreDto> scores = scoreService.getScoresForRound(tournamentId);
        return ResponseEntity.ok(scores);
    }
}
