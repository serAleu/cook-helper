package ru.seraleu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Configuration
@EnableScheduling
@Slf4j
public class CookHelperConfiguration {

    @Value("${telegram.forbidden-words.path}")
    private String telegramForbiddenWordsPath;

    @Bean("gigaResponseStatusesMap")
    public Map<String, List<String>> gigaResponseStatusesMap() {
        return new HashMap<>() {{
            put("SUCCESS", List.of("SUCCESS", "SUCCESS:"));
            put("FAILURE", List.of("FAILURE", "FAILURE:", "GIGACHAT", "ГИГАЧАТ", "НЕЙРОСЕТЬ", "GIGA CHAT", "GIGA_CHAT", "GIGA-CHAT"));
        }};
    }

    @Bean("forbiddenWords")
    public List<String> forbiddenWords() {
        List<String> forbiddenWords = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(telegramForbiddenWordsPath)))) {
            while (reader.ready()) {
                forbiddenWords.add(reader.readLine());
            }
        } catch (Exception e) {
            log.error("Error while forbidden words reading. {}", getStackTrace(e));
        }
        return forbiddenWords;
    }
}
