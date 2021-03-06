package com.elepy.translations;

import com.elepy.exceptions.Translated;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class TranslationsTest {
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @CsvSource({
            "en, hello, {hello}",
            "en, hello, { hello}",
            "en, hello, {hello }",
            "en, hello, { hello }",
            "nl, hallo, {hello}",
            "nl, hallo, { hello}",
            "nl, hallo, {hello }",
            "nl, hallo, { hello }",
            "nl, hallo wereld, { hello} { world}",
            "nl, hallo wereld, { helloWorld }",
            "nl, hallo wereld, { hello.world }",
            "sp, hola mundo, hola mundo",
    })
    void testCanTranslateSimple(String language, String answer, String input) throws JsonProcessingException {
        final var translationModel = new SimpleTranslationModel();

        objectMapper.setLocale(new Locale(language, "NL"));
        translationModel.setToTranslate(input);

        final var output = objectMapper.writeValueAsString(translationModel);

        final var matcher = Pattern.compile("\\{\"toTranslate\":\"(.*)\"}").matcher(output);
        assertThat(matcher.find()).isTrue();
        final var jsonEncodedTranslation = matcher.group(1);

        assertThat(jsonEncodedTranslation)
                .isEqualTo(answer);

    }

    @ParameterizedTest
    @CsvSource({
            "en, hello Ryan, {helloPerson}, Ryan",
            "nl, hallo Ryan, {helloPerson}, Ryan",
    })
    void testCanTranslateAnnotation(String language, String answer, String input, String argument) throws JsonProcessingException {
        final var translationModel = new MessageFormatTranslationModel();

        objectMapper.setLocale(new Locale(language, "NL"));
        translationModel.setToTranslate(Translated.of(input, argument));

        final var output = objectMapper.writeValueAsString(translationModel);

        final var matcher = Pattern.compile("\\{\"toTranslate\":\"(.*)\"}").matcher(output);
        assertThat(matcher.find()).isTrue();
        final var jsonEncodedTranslation = matcher.group(1);

        assertThat(jsonEncodedTranslation)
                .isEqualTo(answer);

    }
}
