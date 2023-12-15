package com.wanted.safewallet.domain.user.web.validation;

import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.passay.CharacterRule;
import org.passay.DictionaryRule;
import org.passay.EnglishCharacterData;
import org.passay.IllegalRegexRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.ResourceBundleMessageResolver;
import org.passay.RuleResult;
import org.passay.UsernameRule;
import org.passay.WhitespaceRule;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;
import org.springframework.util.ResourceUtils;

public class PasswordConstraintValidator implements
    ConstraintValidator<ValidPassword, UserJoinRequest> {

    private static final PasswordValidator validator;
    private static final String COMMON_PASSWORD_FILE = "classpath:static/common-password.txt";
    private static final String KOREAN_REGEX = "[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]+";

    static {
        WordListDictionary wordListDictionary = getWordListDictionary();
        ResourceBundleMessageResolver resolver = new ResourceBundleMessageResolver();
        validator = new PasswordValidator(resolver, List.of(
            new LengthRule(10, 30),
            new CharacterRule(EnglishCharacterData.Alphabetical, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1),
            new IllegalRegexRule(KOREAN_REGEX, false),
            new WhitespaceRule(),
            new UsernameRule(),
            new DictionaryRule(wordListDictionary)
        ));
    }

    static WordListDictionary getWordListDictionary() {
        try (FileReader fileReader = new FileReader(ResourceUtils.getFile(COMMON_PASSWORD_FILE))) {
            return new WordListDictionary(
                WordLists.createFromReader(new FileReader[]{fileReader}, false, new ArraysSort()));
        } catch (IOException e) {
            return new WordListDictionary(new ArrayWordList(new String[]{}));
        }
    }

    @Override
    public boolean isValid(UserJoinRequest request, ConstraintValidatorContext context) {
        PasswordData passwordData = new PasswordData(request.getUsername(), request.getPassword());
        RuleResult result = validator.validate(passwordData);
        if (result.isValid()) {
            return true;
        }

        String messageTemplate = String.join(" ", validator.getMessages(result));
        context.buildConstraintViolationWithTemplate(messageTemplate)
            .addConstraintViolation()
            .disableDefaultConstraintViolation();
        return false;
    }
}
