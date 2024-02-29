package be.technobel.chesstournament.pl.controllers;

import be.technobel.chesstournament.bll.services.MatchService;
import be.technobel.chesstournament.dal.models.enums.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}")
    public void modifyMatchResult(Long matchId, Result result){
        matchService.modifyMatchResult(matchId,result);
    }
}
