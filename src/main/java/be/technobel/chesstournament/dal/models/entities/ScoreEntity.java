package be.technobel.chesstournament.dal.models.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class ScoreEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity player;

    private int matchesPlayed;
    private int victories;
    private int defeats;
    private int draws;
    private double score;

    @OneToOne
    @JoinColumn(name = "match_id")
    private MatchEntity match;
}
