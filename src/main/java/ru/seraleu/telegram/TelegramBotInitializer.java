package ru.seraleu.telegram;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.seraleu.telegram.services.TelegramMessageProcessor;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Component
@DependsOn({"telegramMessageProcessor"})
@Profile("telegram")
@RequiredArgsConstructor
@Slf4j
public class TelegramBotInitializer {

    private final TelegramBotsApi telegramBotsApi;
    private final TelegramMessageProcessor telegramMessageProcessor;

    @PostConstruct
    public void init() {
        try {
            telegramBotsApi.registerBot(telegramMessageProcessor);
        } catch (TelegramApiException e) {
            log.error("Error while cookassistbot initialization. {}", getStackTrace(e));
        }
    }
}
