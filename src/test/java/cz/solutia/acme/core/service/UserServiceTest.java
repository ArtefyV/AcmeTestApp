package cz.solutia.acme.core.service;

import cz.solutia.acme.core.dto.PasswordChangeDTO;
import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.repository.UserRepository;
import cz.solutia.acme.core.validator.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordValidator passwordValidator;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordValidator = mock(PasswordValidator.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        userService = new UserService(userRepository, passwordValidator, bCryptPasswordEncoder);
    }

    @Test
    void changePassword_Success() {

        User user = new User();
        user.setId(1);
        user.setPassword("encodedOldPassword");

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword("oldPassword");
        passwordChangeDTO.setNewPassword("newPassword123");
        passwordChangeDTO.setConfirmPassword("newPassword123");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordValidator.validate("newPassword123")).thenReturn(List.of());

        List<String> result = userService.changePassword(1, passwordChangeDTO);

        assertEquals(0, result.size());
        verify(userRepository).save(user);
        verify(bCryptPasswordEncoder).encode("newPassword123");
    }

    @Test
    void changePassword_UserNotFound() {
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        List<String> result = userService.changePassword(1, passwordChangeDTO);

        assertEquals(1, result.size());
        assertEquals("User not found", result.get(0));
    }

    @Test
    void changePassword_CurrentPasswordIncorrect() {
        User user = new User();
        user.setId(1);
        user.setPassword("encodedOldPassword");

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword("wrongPassword");
        passwordChangeDTO.setNewPassword("newPassword123");
        passwordChangeDTO.setConfirmPassword("newPassword123");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        List<String> result = userService.changePassword(1, passwordChangeDTO);

        assertEquals(1, result.size());
        assertEquals("Current password is incorrect", result.get(0));
    }

    @Test
    void changePassword_NewPasswordMismatch() {
        User user = new User();
        user.setId(1);
        user.setPassword("encodedOldPassword");

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword("oldPassword");
        passwordChangeDTO.setNewPassword("newPassword123");
        passwordChangeDTO.setConfirmPassword("differentPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);

        List<String> result = userService.changePassword(1, passwordChangeDTO);

        assertEquals(1, result.size());
        assertEquals("New password and confirmation do not match", result.get(0));
    }

    @Test
    void changePassword_NewPasswordValidationFails() {
        User user = new User();
        user.setId(1);
        user.setPassword("encodedOldPassword");

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword("oldPassword");
        passwordChangeDTO.setNewPassword("weak");
        passwordChangeDTO.setConfirmPassword("weak");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordValidator.validate("weak")).thenReturn(List.of("Password is too weak"));

        List<String> result = userService.changePassword(1, passwordChangeDTO);

        assertEquals(1, result.size());
        assertEquals("Password is too weak", result.get(0));
    }
}