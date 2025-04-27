package cz.solutia.acme.core.controller;

import cz.solutia.acme.core.dto.PasswordChangeDTO;
import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.repository.GeneralSettingsRepository;
import cz.solutia.acme.core.repository.UserRepository;
import cz.solutia.acme.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class SettingsController {
    private GeneralSettingsRepository generalSettingsRepository;

    private UserRepository userRepository;
    private final UserService userService;

    public SettingsController(GeneralSettingsRepository generalSettingsRepository, UserRepository userRepository, UserService userService) {
        this.generalSettingsRepository = generalSettingsRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Setup for general settings page
     * @param model
     * @return
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("menu", "settings");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String firstname = "";
        String lastname = "";
        String email = authentication.getName();
        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isPresent()) {
            firstname = user.get().getFirstname();
            lastname = user.get().getLastname();
        }

        model.addAttribute("firstname", firstname);
        model.addAttribute("lastname", lastname);
        model.addAttribute("email", email);
        return "settings";
    }


    /**
     * Change user password
     * @param passwordChangeDTO - DTO containing current and new passwords
     * @param redirectAttributes - redirect attributes to pass messages
     * @return String - redirect to settings page
     */
    @PostMapping("/settings/password")
    public String changePassword(@ModelAttribute PasswordChangeDTO passwordChangeDTO, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {
            List<String> errors = userService.changePassword(user.get().getId(), passwordChangeDTO);

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/settings";
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
        return "redirect:/settings";
    }
}
