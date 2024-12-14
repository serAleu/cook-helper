package ru.seraleu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class TelegramConfiguration {

    @Value("${web.telegram.auth.token}")
    private String telegramBotToken;

    @Bean("telegramBotToken")
    public String telegramBotToken() {
        return telegramBotToken;
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