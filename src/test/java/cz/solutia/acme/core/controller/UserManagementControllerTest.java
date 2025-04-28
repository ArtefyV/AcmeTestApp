package cz.solutia.acme.core.controller;

import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserManagementControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Model model;

    @InjectMocks
    private UserManagementController userManagementController;

    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userManagementController = new UserManagementController(mailSender, userService);
    }

    @Test
    void testPasswordResetAction_UserFound_Success() {
        User user = new User();
        user.setEmail(testEmail);
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(user));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        doNothing().when(userService).savePassword(any(User.class), anyString());

        String result = userManagementController.passwordResetAction(testEmail, model);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(userService, times(1)).savePassword(eq(user), anyString());
        verify(model, times(1)).addAttribute(eq("successMessage"), anyString());
        assertEquals("login", result);
    }

    @Test
    void testPasswordResetAction_UserNotFound_Error() {
        when(userService.findByEmail(testEmail)).thenReturn(Optional.empty());

        String result = userManagementController.passwordResetAction(testEmail, model);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verify(userService, never()).savePassword(any(User.class), anyString());
        verify(model, times(1)).addAttribute(eq("errorMessage"), contains("does not exists"));
        assertEquals("password-reset", result);
    }

    @Test
    void testPasswordResetAction_UserFound_EmailSendFails() {
        User user = new User();
        user.setEmail(testEmail);
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Mail sending failed")).when(mailSender).send(any(SimpleMailMessage.class));

        String result = userManagementController.passwordResetAction(testEmail, model);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(userService, never()).savePassword(any(User.class), anyString());
        verify(model, times(1)).addAttribute(eq("errorMessage"), contains("Unable to send the temporary password email"));
        assertEquals("password-reset", result);
    }
}