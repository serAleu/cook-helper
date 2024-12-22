package ru.seraleu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Configuration
@Slf4j
public class TelegramConfiguration {

    @Value("${telegram.swearing.path}")
    private String telegramSwearingPath;

    @Value("${telegram.web.auth.token}")
    private String telegramBotToken;

    @Bean("telegramBotToken")
    public String telegramBotToken() {
        return telegramBotToken;
    }

    @Bean("swearings")
    public List<String> swearings() {
        List<String> swearings = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(telegramSwearingPath)))) {
            while (reader.ready()) {
                swearings.add(reader.readLine());
            }
        } catch (Exception e) {
            log.error("Error while swearings reading. {}", getStackTrace(e));
        }
        return swearings;
    }

    @Bean("telegramBotsApi")
    public TelegramBotsApi telegramBotsApi() {
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return telegramBotsApi;
    }
}