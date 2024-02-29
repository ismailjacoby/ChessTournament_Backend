package be.technobel.chesstournament.bll.services;

import be.technobel.chesstournament.pl.models.dtos.AuthDto;
import be.technobel.chesstournament.pl.models.forms.LoginForm;
import be.technobel.chesstournament.pl.models.forms.UserForm;

public interface UserService {
    void registerPlayer(UserForm form);
    AuthDto login(LoginForm form);
}
