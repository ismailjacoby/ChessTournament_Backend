package be.technobel.chesstournament.pl.models.forms;

import be.technobel.chesstournament.dal.models.enums.UserCategory;
import be.technobel.chesstournament.dal.models.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record TournamentForm(
            String name,
            String location,
            @Min(2)@Max(32)@NotNull
            int minPlayers,
            @Min(2)@Max(32)@NotNull
            int maxPlayers,
            @Min(0)@Max(3000)
            Integer minElo,
            @Min(0)@Max(3000)
            Integer maxElo,
            UserCategory category,
            Status status,
            int currentRound,
            boolean womenOnly,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date registrationEndDate
) {
}
