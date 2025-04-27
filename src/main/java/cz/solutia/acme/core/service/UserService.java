package cz.solutia.acme.core.service;

import cz.solutia.acme.core.dto.PasswordChangeDTO;
import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.repository.UserRepository;
import cz.solutia.acme.core.validator.PasswordValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, PasswordValidator passwordValidator, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.passwordValidator = passwordValidator;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Find user by username
     * @param username
     * @return Optional<User>
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by email
     * @param email
     * @return Optional<User>
     */
    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    /**
     * Change user password
     * @param userId
     * @param passwordChangeDTO
     * @return List<String> - list of error messages
     */
    public List<String> changePassword(Integer userId, PasswordChangeDTO passwordChangeDTO) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return List.of("User not found");
        }

        User user = userOptional.get();

        // Checking the current password
        if (!bCryptPasswordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            return List.of("Current password is incorrect");
        }

        // Checking if the new password and confirmation match
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            return List.of("New password and confirmation do not match");
        }

        // Validation of password complexity
        List<String> passwordErrors = passwordValidator.validate(passwordChangeDTO.getNewPassword());
        if (!passwordErrors.isEmpty()) {
            return passwordErrors;
        }

        // Saving a new password
        savePassword(user, passwordChangeDTO.getNewPassword());

        return List.of(); // An empty list means successful execution
    }

    /**
     * Encrypting and saving a password for the user
     * @param user
     * @param password
     */
    public void savePassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }
}
