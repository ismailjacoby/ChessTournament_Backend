package be.technobel.chesstournament.dal.models.entities;

import be.technobel.chesstournament.dal.models.enums.UserCategory;
import be.technobel.chesstournament.dal.models.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity @Data
public class TournamentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tournament_id")
    private Long id;

    private String name;
    @Column(nullable = true)
    private String location;
    private int minPlayers;
    private int maxPlayers;
    @Column(nullable = true)
    private int minElo;
    @Column(nullable = true)
    private int maxElo;

    @Enumerated(EnumType.STRING)
    private UserCategory category;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int currentRound;
    @Getter
    private boolean womenOnly;
    private Date creationDate;
    private Date registrationEndDate;
    private Date updateDate;

    private int nbOfPlayersRegistered;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "tournament_participants",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> participants = new HashSet<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private Set<MatchEntity> matches;

    public void setMinPlayers(int minPlayers) {
        if (minPlayers < 2){
            throw new IllegalArgumentException("There must be at least 2 players in the tournament.");
        } else if (minPlayers > 32) {
            throw new IllegalArgumentException("There must be less than 32 players in the tournament.");
        }
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        if (maxPlayers < 2){
            throw new IllegalArgumentException("Max Players must be at least 2.");
        } else if (maxPlayers > 32) {
            throw new IllegalArgumentException("There must be less than 32 players in the tournament.");
        }
        this.maxPlayers = maxPlayers;
    }


    public void setMinElo(int minElo) {
        if (minElo<0){
            throw new IllegalArgumentException("Minimum Elo must be 0 or Higher");
        } else if (minElo>3000) {
            throw new IllegalArgumentException("Minimum Elo must be lower than 3000.");
        }
        this.minElo = minElo;
    }

    public void setMaxElo(int maxElo) {
        if (maxElo<0){
            throw new IllegalArgumentException("Minimum Elo must be 0 or Higher");
        } else if (maxElo>3000) {
            throw new IllegalArgumentException("Minimum Elo must be lower than 3000.");
        }
        this.maxElo = maxElo;
    }

    public void removeParticipant(UserEntity user) {
        participants.remove(user);
        user.getTournaments().remove(this);
    }


}
