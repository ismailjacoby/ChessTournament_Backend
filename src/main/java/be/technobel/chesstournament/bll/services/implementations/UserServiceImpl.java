package be.technobel.chesstournament.bll.services.implementations;

import be.technobel.chesstournament.bll.mailing.EmailSenderService;
import be.technobel.chesstournament.bll.services.UserService;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Role;
import be.technobel.chesstournament.dal.repositories.UserRepository;
import be.technobel.chesstournament.pl.config.security.JWTProvider;
import be.technobel.chesstournament.pl.models.dtos.AuthDto;
import be.technobel.chesstournament.pl.models.forms.LoginForm;
import be.technobel.chesstournament.pl.models.forms.UserForm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder encoder;



    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService, AuthenticationManager authenticationManager, JWTProvider jwtProvider, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.encoder = encoder;
    }

    /**
     * Login method for authenticating a user and generating an authentication token.
     *
     * @param form The login form containing user credentials.
     * @return An AuthDto containing the generated JWT token, username, and role.
     */
    @Override
    public AuthDto login(LoginForm form) {
        // Authenticate user credentials
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.getUsername(),form.getPassword()));

        // Retrieve user information
        UserEntity user = userRepository.findByUsername(form.getUsername()).get();

        // Generate JWT token
        String token = jwtProvider.generateToken(user.getUsername(), user.getRole());

        // Build and return AuthDto
        return AuthDto.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    /**
     * Register a new player, generating a random password, encoding it, and sending a registration email.
     *
     * @param form The user registration form.
     */
    @Override
    public void registerPlayer(UserForm form) {
        if(form==null){
            throw new IllegalArgumentException("Form can't be null.");
        }

        // Generate a random password for the new user
        String randomPassword = generateRandomPassword();

        // Create a new UserEntity and populate with registration data
        UserEntity player = new UserEntity();
        player.setUsername(form.username());
        player.setEmail(form.email());
        player.setDateOfBirth(form.dateOfBirth());
        player.setGender(form.gender());
        player.setRole(Role.PLAYER);
        player.setElo(form.elo());
        player.setPassword(passwordEncoder.encode(randomPassword));
        player.setEnabled(true);

        // Validate the user registration form
        validateUserForm(form);

        // Save the new user to the repository
        userRepository.save(player);

        // Send a registration email with the generated password
        emailSenderService.sendRegistrationEmail(player.getEmail(), player.getUsername(), randomPassword);
    }

    /**
     * Method to generate a new random password.
     *
     * @return A randomly generated alphanumeric password.
     */
    private String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    /**
     * Hashes a password by combining it with a salt and using BCryptPasswordEncoder.
     *
     * @param password The plain text password to be hashed.
     * @param salt     The salt to be combined with the password before hashing.
     * @return A hashed representation of the password.
     */
    private String hashPassword(String password, String salt) {
        // Create a new instance of BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Combine the password with the salt and hash it using BCrypt
        return passwordEncoder.encode(password + salt);
    }

    /**
     * Validate the user registration form, checking for a valid email format and a birthdate in the past.
     *
     * @param form The user registration form.
     */
    private void validateUserForm(UserForm form) {
        if (!isValidEmail(form.email())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (form.dateOfBirth().after(new Date())) {
            throw new IllegalArgumentException("Birthdate has to be in the past.");
        }
    }

    /**
     * Check if the provided email is in a valid format.
     *
     * @param email The email address to validate.
     * @return True if the email is in a valid format, false otherwise.
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

}
