package be.technobel.chesstournament.pl.models.forms;

import be.technobel.chesstournament.dal.models.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.Date;

public record UserForm(
        @NotBlank(message = "Username is required")
        String username,
        @Email(message = "Invalid email address")
        String email,
        @NotNull(message = "Date of birth is required")
        Date dateOfBirth,
        @NotNull(message = "Gender is required")
        Gender gender,
        int elo
) {
}
