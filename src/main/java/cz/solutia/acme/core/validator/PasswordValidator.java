package cz.solutia.acme.core.validator;


import org.passay.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PasswordValidator {
    public List<String> validate(String password) {
        org.passay.PasswordValidator validator = new org.passay.PasswordValidator(Arrays.asList(
                new LengthRule(8, 100),               // не менее 8 символов
                new CharacterRule(EnglishCharacterData.UpperCase, 1),  // хотя бы одна заглавная буква
                new CharacterRule(EnglishCharacterData.LowerCase, 1),  // хотя бы одна строчная буква
                new CharacterRule(EnglishCharacterData.Digit, 1),      // хотя бы одна цифра
                new CharacterRule(EnglishCharacterData.Special, 1),    // хотя бы один спецсимвол
                new WhitespaceRule()                  // без пробелов
        ));

        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return new ArrayList<>();
        }

        return validator.getMessages(result);
    }

    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }
}
