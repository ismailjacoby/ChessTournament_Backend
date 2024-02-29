package be.technobel.chesstournament.dal.models.entities;

import be.technobel.chesstournament.dal.models.enums.Result;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class MatchEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private TournamentEntity tournament;

    private int round;

    @ManyToOne
    @JoinColumn(name = "white_id")
    private UserEntity player1;

    @ManyToOne
    @JoinColumn(name = "black_id")
    private UserEntity player2;

    @OneToOne(mappedBy = "match")
    private ScoreEntity player1Score;

    @OneToOne(mappedBy = "match")
    private ScoreEntity player2Score;

    private Result result;

}
