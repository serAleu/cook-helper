package ru.seraleu.telegram.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.seraleu.telegram.users.TelegramCommunicationStep;
import ru.seraleu.telegram.users.TelegramUser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.services.TelegramMessageProcessor.TELEGRAM_USERS_MAP;
import static ru.seraleu.telegram.users.TelegramCommunicationStep.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramUtils {

    private final List<String> forbiddenWords;

    @Value("${telegram.start.button}")
    private String telegramStartButton;
    @Value("${telegram.start.message}")
    private String telegramStartMessage;
    @Value("${telegram.start.image-path}")
    private String telegramStartImagePath;
    @Value("${gigachat.web.error-messages-for-user.stupid-user}")
    private String gigachatWebErrorsStupidUser;

    public void updateTelegramUserMap(String message, TelegramCommunicationStep step, TelegramUser user) {
        user.getCommunications().put(step, message);
        user.setCurrentCommunicationStep(step);
        TELEGRAM_USERS_MAP.put(user.getChatId(), user);
    }

    void defineMessageText(TelegramUser user, SendMessage message) {
        switch (user.getCurrentCommunicationStep() != null ? user.getCurrentCommunicationStep() : NO_RESPONSE) {
            case STUPID_GIGA_RESPONSE -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(STUPID_GIGA_RESPONSE));
            case STUPID_USER_RESPONSE -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(STUPID_USER_RESPONSE));
            case DISHES_RESPONSE -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", вот, что я для тебя нашел: " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(DISHES_RESPONSE));
            default -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(NO_RESPONSE));
        }
    }

    boolean isRequestContainForbiddenWord(String requestText) {
        AtomicBoolean isContain = new AtomicBoolean(false);
        forbiddenWords.forEach(forbiddenWord -> {
            if (StringUtils.containsIgnoreCase(requestText, forbiddenWord)) {
                isContain.set(true);
            }
        });
        return isContain.get();
    }

    SendPhoto startBot(Update update) {
        try {
            String startMessage = "Привет, " + update.getMessage().getFrom().getFirstName() + "! " + telegramStartMessage;
            return SendPhoto.builder()
                    .chatId(update.getMessage().getChatId())
                    .photo(new InputFile(new File(telegramStartImagePath)))
                    .caption(startMessage)
                    .replyMarkup(getStartButtonReplyMarkup())
                    .build();
        } catch (Exception e) {
            log.error("Exception while start-message with photo sending. e = {}", getStackTrace(e));
            return SendPhoto.builder()
                    .chatId(update.getMessage().getChatId())
                    .caption("Привет, " + update.getMessage().getFrom().getFirstName() + "! " + "Эта сучка сломалась.")
                    .build();
        }
    }

    InlineKeyboardMarkup getStartButtonReplyMarkup() {
        InlineKeyboardButton startButton1 = new InlineKeyboardButton();
        startButton1.setText(telegramStartButton);
        startButton1.setCallbackData("Че надо петушара");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(startButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    void addUser(Update update) {
        TelegramUser user = TELEGRAM_USERS_MAP.get(update.getMessage().getChatId());
        if (user == null || StringUtils.isEmpty(user.getUserName()) || user.getChatId() == null) {
            user = new TelegramUser()
                    .setUserName(update.getMessage().getFrom().getFirstName())
                    .setChatId(update.getMessage().getChatId());
        }
        if(!isRequestContainForbiddenWord(update.getMessage().getText())) {
            user.setCommunications(new HashMap<>() {{
                put(SLOVOTBIRATOR_REQUEST, update.getMessage().getText());
            }}).setCurrentCommunicationStep(SLOVOTBIRATOR_REQUEST);
            updateTelegramUserMap(update.getMessage().getText(), SLOVOTBIRATOR_REQUEST, user);
        } else {
            user.setCommunications(new HashMap<>() {{
                put(STUPID_USER_RESPONSE, update.getMessage().getText());
            }}).setCurrentCommunicationStep(STUPID_USER_RESPONSE);
            updateTelegramUserMap(gigachatWebErrorsStupidUser, STUPID_USER_RESPONSE, user);
        }
    }
}
