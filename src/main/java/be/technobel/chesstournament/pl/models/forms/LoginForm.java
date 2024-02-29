package be.technobel.chesstournament.pl.models.forms;

import be.technobel.chesstournament.dal.models.enums.Gender;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LoginForm {
    private String username;
    private String password;
}
