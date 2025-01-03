package ru.seraleu.telegram.utils;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.services.TelegramMessageProcessor.TELEGRAM_USERS_MAP;
import static ru.seraleu.telegram.users.TelegramCommunicationStep.*;
import static ru.seraleu.telegram.utils.Callback.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramUtils {

    private final List<String> forbiddenWords;

    @Value("${telegram.steps.start.button}")
    private String telegramStepsStartButton;
    @Value("${telegram.steps.start.message}")
    private String telegramStepsStartMessage;
    @Value("${telegram.steps.start.image-path}")
    private String telegramStepsStartImagePath;
    @Value("${gigachat.web.error-messages-for-user.stupid-user}")
    private String gigachatWebErrorsStupidUser;
    @Value("${telegram.steps.options.dishes.button}")
    private String telegramStepsOptionsDishesButton;
    @Value("${telegram.steps.options.option1.button}")
    private String telegramStepsOptionsOption1Button;
    @Value("${telegram.steps.options.option2.button}")
    private String telegramStepsOptionsOption2Button;


    public void updateTelegramUserMap(String message, TelegramCommunicationStep step, TelegramUser user) {
        if(user.getCommunications() != null) {
            user.getCommunications().put(step, message);
        } else {
            user.setCommunications(new HashMap<>() {{
                put(step, message);
            }});
        }
        user.getCommunications().put(step, message);
        user.setCurrentCommunicationStep(step);
        TELEGRAM_USERS_MAP.put(user.getChatId(), user);
    }

    public void defineMessageText(TelegramUser user, SendMessage message) {
        switch (user.getCurrentCommunicationStep() != null ? user.getCurrentCommunicationStep() : NO_RESPONSE) {
            case STUPID_GIGA_RESPONSE -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(STUPID_GIGA_RESPONSE));
            case STUPID_USER_RESPONSE -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(STUPID_USER_RESPONSE));
            case DISHES_RESPONSE -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", вот, что я для тебя нашел: " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(DISHES_RESPONSE));
            default -> message.setText(TELEGRAM_USERS_MAP.get(user.getChatId()).getUserName() + ", " + TELEGRAM_USERS_MAP.get(user.getChatId()).getCommunications().get(NO_RESPONSE));
        }
    }

    public boolean isRequestContainForbiddenWord(String requestText) {
        AtomicBoolean isContain = new AtomicBoolean(false);
        forbiddenWords.forEach(forbiddenWord -> {
            if (StringUtils.containsIgnoreCase(requestText, forbiddenWord)) {
                isContain.set(true);
            }
        });
        return isContain.get();
    }

    public SendPhoto startBot(Update update) {
        try {
            String startMessage = "Привет, " + update.getMessage().getFrom().getFirstName() + "! " + telegramStepsStartMessage;
            return SendPhoto.builder()
                    .chatId(update.getMessage().getChatId())
                    .photo(new InputFile(new File(telegramStepsStartImagePath)))
                    .caption(startMessage)
                    .replyMarkup(getStartButton())
                    .build();
        } catch (Exception e) {
            log.error("Exception while start-message with photo sending. e = {}", getStackTrace(e));
            return SendPhoto.builder()
                    .chatId(update.getMessage().getChatId())
                    .caption("Привет, " + update.getMessage().getFrom().getFirstName() + "! " + "Эта сучка сломалась.")
                    .build();
        }
    }

    public InlineKeyboardMarkup getStartButton() {
        InlineKeyboardButton startButton = new InlineKeyboardButton();
        startButton.setCallbackData(START.getPath());
        startButton.setText(telegramStepsStartButton);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(startButton);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getOptionsButtons() {
        InlineKeyboardButton dishesButton = new InlineKeyboardButton();
        dishesButton.setCallbackData(DISHES.getPath());
        dishesButton.setText(telegramStepsOptionsDishesButton);

        InlineKeyboardButton option1Button = new InlineKeyboardButton();
        option1Button.setCallbackData(OPTION1.getPath());
        option1Button.setText(telegramStepsOptionsOption1Button);

        InlineKeyboardButton option2Button = new InlineKeyboardButton();
        option2Button.setCallbackData(OPTION2.getPath());
        option2Button.setText(telegramStepsOptionsOption2Button);

        List<InlineKeyboardButton> keyboardButtonsRow1 = Collections.singletonList(dishesButton);
        List<InlineKeyboardButton> keyboardButtonsRow2 = Collections.singletonList(option1Button);
        List<InlineKeyboardButton> keyboardButtonsRow3 = Collections.singletonList(option2Button);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public void addUser(Update update, Long chatId) {
        TelegramUser user = TELEGRAM_USERS_MAP.get(chatId);
        if (user == null || StringUtils.isEmpty(user.getUserName()) || user.getChatId() == null) {
            if(update.hasMessage()) {
                user = new TelegramUser().setUserName(update.getMessage().getFrom().getFirstName());
            } else if (update.hasCallbackQuery()) {
                user = new TelegramUser().setUserName(update.getCallbackQuery().getFrom().getUserName());
            } else return;
            user.setChatId(chatId);
        }
        if(update.hasMessage() && isRequestContainForbiddenWord(update.getMessage().getText())) {
            user.setCommunications(new HashMap<>() {{
                put(STUPID_USER_RESPONSE, update.getMessage().getText());
            }}).setCurrentCommunicationStep(STUPID_USER_RESPONSE);
            updateTelegramUserMap(gigachatWebErrorsStupidUser, STUPID_USER_RESPONSE, user);
        }
        TELEGRAM_USERS_MAP.put(user.getChatId(), user);
    }
}
