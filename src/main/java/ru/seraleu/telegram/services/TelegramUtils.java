package ru.seraleu.telegram.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.seraleu.telegram.users.TelegramUser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.services.TelegramMessageProcessor.TELEGRAM_USERS_MAP;
import static ru.seraleu.telegram.users.TelegramRequestType.INITIAL_REQUEST;
import static ru.seraleu.telegram.users.TelegramRequestType.LIST_OF_PRODUCT_REQUEST;
import static ru.seraleu.telegram.users.TelegramResponseType.NO_RESPONSE;
import static ru.seraleu.telegram.users.TelegramResponseType.START_RESPONSE;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramUtils {

    private final List<String> swearings;

    @Value("${telegram.start.button}")
    private String telegramStartButton;
    @Value("${telegram.start.message}")
    private String telegramStartMessage;
    @Value("${telegram.start.image-path}")
    private String telegramStartImagePath;

    boolean isRequestContainSwearing(String requestText) {
        AtomicBoolean isContain = new AtomicBoolean(false);
        swearings.forEach(swearing -> {
            if(StringUtils.containsIgnoreCase(requestText, swearing)) {
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
        startButton1.setCallbackData("HZ1");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(startButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    void updateTelegramUserMap(Update update) {
        TelegramUser user = TELEGRAM_USERS_MAP.get(update.getMessage().getChatId());
        if(user == null || StringUtils.isEmpty(user.getUserName()) || user.getChatId() == null) {
            user = new TelegramUser()
                    .setUserName(update.getMessage().getFrom().getFirstName())
                    .setChatId(update.getMessage().getChatId())
                    .setRequestsMap(new HashMap<>() {{
                        put(INITIAL_REQUEST, update.getMessage().getText());
                        put(LIST_OF_PRODUCT_REQUEST, update.getMessage().getText());
                    }})
                    .setResponsesMap(new HashMap<>() {{
                        put(NO_RESPONSE, "no response");
                        put(START_RESPONSE, telegramStartMessage);
                    }});
            TELEGRAM_USERS_MAP.put(user.getChatId(), user);
        }
    }

}
