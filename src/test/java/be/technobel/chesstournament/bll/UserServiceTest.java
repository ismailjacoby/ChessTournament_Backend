package be.technobel.chesstournament.bll;

import be.technobel.chesstournament.bll.mailing.EmailSenderService;
import be.technobel.chesstournament.bll.services.UserService;
import be.technobel.chesstournament.bll.services.implementations.UserServiceImpl;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Gender;
import be.technobel.chesstournament.dal.repositories.UserRepository;
import be.technobel.chesstournament.pl.models.forms.UserForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //1. Register Player Tests
    @Test
    void registerPlayer_Success() {
        // Arrange
        UserForm userForm = new UserForm(
                "john_doe",
                "john@example.com",
                new Date(),
                Gender.MALE,
                1500);

        // Mock passwordEncoder and userRepository behavior
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(new UserEntity());

        // Act
        userService.registerPlayer(userForm);

        // Assert
        verify(userRepository, times(1)).save(any());
        verify(emailSenderService, times(1)).sendRegistrationEmail(any(), any(), any());
    }

    @Test
    void registerPlayer_ValidForm_SavesUserAndSendsEmail() {
        // Arrange
        UserForm userForm = new UserForm("john_doe", "john@example.com", new Date(), Gender.MALE, 1500);

        // Mock passwordEncoder and userRepository behavior
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(new UserEntity());

        // Act
        userService.registerPlayer(userForm);

        // Assert
        verify(userRepository, times(1)).save(any());
        verify(emailSenderService, times(1)).sendRegistrationEmail(any(), any(), any());
    }

    @Test
    void registerPlayer_NullForm_ThrowsIllegalArgumentException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerPlayer(null));
    }

    @Test
    void registerPlayer_InvalidEmailFormat_ThrowsIllegalArgumentException() {
        // Arrange
        UserForm userForm = new UserForm("john_doe", "invalid_email", new Date(), Gender.MALE, 1500);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerPlayer(userForm));
    }

    @Test
    void registerPlayer_DateOfBirthInFuture_ThrowsIllegalArgumentException() {
        // Arrange
        UserForm userForm = new UserForm("john_doe", "john@example.com", new Date(System.currentTimeMillis() + 86400000), Gender.MALE, 1500); // One day in the future

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerPlayer(userForm));
    }

    @Test
    void registerPlayer_ZeroElo_DefaultsTo1200() {
        // Arrange
        UserForm userForm = new UserForm("john_doe", "john@example.com", new Date(), Gender.MALE, 0);

        // Mock passwordEncoder and userRepository behavior
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(new UserEntity());

        // Act
        userService.registerPlayer(userForm);

        // Assert
        verify(userRepository, times(1)).save(any());
        verify(emailSenderService, times(1)).sendRegistrationEmail(any(), any(), any());

        // Verify that the elo was set to the default value (1200)
        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityCaptor.capture());
        assertEquals(1200, userEntityCaptor.getValue().getElo());
    }

    @Test
    void registerPlayer_MinElo_Above3000_ThrowsIllegalArgumentException() {
        // Arrange
        UserForm userForm = new UserForm("john_doe", "john@example.com", new Date(), Gender.MALE, 3500);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerPlayer(userForm));
    }

    @Test
    void registerPlayer_MinElo_UnderZero_ThrowsIllegalArgumentException() {
        // Arrange
        UserForm userForm = new UserForm("john_doe", "john@example.com", new Date(), Gender.MALE, -1);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerPlayer(userForm));
    }

}
