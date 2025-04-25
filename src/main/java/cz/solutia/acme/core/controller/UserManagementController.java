package cz.solutia.acme.core.controller;

import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.UUID;

@Controller
public class UserManagementController {
    private static final Logger LOGGER = LogManager.getLogger(UserManagementController.class);

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String mailUsername;

    private final UserService userService;
    private final JavaMailSender mailSender;

    @Autowired
    public UserManagementController(JavaMailSender mailSender, UserService userService)
    {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    /**
     * Action for login
     * @return String - redirect to the login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Action for redirect to password reset
     * @return String - redirect to the password reset page
     */
    @GetMapping("/password-reset")
    public String passwordReset() {
        return "password-reset";
    }

    /**
     * Action for password reset
     * @param email - email of the user
     * @param model - model to add attributes
     * @return String - redirect to login page
     */
    @PostMapping("/password-reset")
    public String passwordResetAction(@Valid @ModelAttribute("email") String email, Model model) {
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "An account with the email "+email+" does not exists, we cannot reset your password.");
            return "password-reset";
        }

        User user = userOptional.get();
        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("The temporary password: " + temporaryPassword); // For debugging purposes
        userService.saveTemporaryPassword(user, temporaryPassword);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Password Reset");
            message.setFrom(mailUsername);
            message.setText("Your temporary password is: " + temporaryPassword + "\nPlease log in and change your password.");
            mailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("Unable to send the password reset email", e);
            model.addAttribute("errorMessage", "Unable to send the temporary password email.");
            return "password-reset";
        }

        model.addAttribute("successMessage", "If an account with the email "+email+" exists, we've sent you a link to reset your password.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
}
