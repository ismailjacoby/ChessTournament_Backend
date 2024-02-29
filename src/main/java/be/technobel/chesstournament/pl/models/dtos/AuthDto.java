package be.technobel.chesstournament.pl.models.dtos;

import be.technobel.chesstournament.dal.models.enums.Gender;
import be.technobel.chesstournament.dal.models.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthDto {
    private String username;
    private String token;
    private Role role;

}
