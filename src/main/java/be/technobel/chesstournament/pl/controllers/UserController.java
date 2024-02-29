package be.technobel.chesstournament.pl.controllers;

import be.technobel.chesstournament.bll.services.UserService;
import be.technobel.chesstournament.pl.models.dtos.AuthDto;
import be.technobel.chesstournament.pl.models.forms.LoginForm;
import be.technobel.chesstournament.pl.models.forms.UserForm;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public void register(@RequestBody @Valid UserForm form){
        userService.registerPlayer(form);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public AuthDto login(@RequestBody LoginForm form) {
        return userService.login(form);
    }
}
