package cz.solutia.acme.core.validator;

import org.passay.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PasswordValidator {

    @Value("${password.min-length}")
    private int minLength;

    @Value("${password.max-length}")
    private int maxLength;

    /**
     * Validates the password according to the rules:
     * - at least 8 characters
     * - at least one capital letter
     * - at least one lowercase letter
     * - at least one digit
     * - at least one wildcard
     * - line without spaces
     *
     * @param password the password to validate
     * @return a list of error messages if the password is invalid, otherwise an empty list
     */
    public List<String> validate(String password) {
        org.passay.PasswordValidator validator = new org.passay.PasswordValidator(Arrays.asList(
                new LengthRule(minLength, maxLength),                                     // at least 8 characters
                new CharacterRule(EnglishCharacterData.UpperCase, 1),  // has at least one capital letter
                new CharacterRule(EnglishCharacterData.LowerCase, 1),  // has at least one lowercase letter
                new CharacterRule(EnglishCharacterData.Digit, 1),      // has at least one digit
                new CharacterRule(EnglishCharacterData.Special, 1),    // has at least one wildcard
                new WhitespaceRule()                                        // line without spaces
        ));

        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return new ArrayList<>();
        }

        return validator.getMessages(result);
    }

    /**
     * Checks if the password is valid according to the rules
     *
     * @param password the password to check
     * @return true if the password is valid, false otherwise
     */
    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }
}
