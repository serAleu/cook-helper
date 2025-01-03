package ru.seraleu.telegram.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.seraleu.gigachat.services.GigachatClientService;
import ru.seraleu.telegram.users.TelegramUser;
import ru.seraleu.telegram.utils.TelegramUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.users.TelegramCommunicationStep.*;
import static ru.seraleu.telegram.utils.Callback.*;

@Slf4j
@RequiredArgsConstructor
@Service("telegramMessageProcessor")
public class TelegramMessageProcessor extends TelegramLongPollingBot {

    public static final Map<Long, TelegramUser> TELEGRAM_USERS_MAP = new HashMap<>();

    private final String telegramBotToken;
    private final GigachatClientService gigachatClientService;
    private final TelegramUtils telegramUtils;

    @Value("${telegram.web.bot-username}")
    private String webTelegramBotUsername;
    @Value("${telegram.web.admin-chat-id}")
    private Long webTelegramAdminChatId;
    @Value("${telegram.steps.options.message}")
    private String telegramStepsOptionsMessage;
    @Value("${telegram.steps.options.dishes.message}")
    private String telegramStepsOptionsDishesMessage;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().getChatId() != null && update.getMessage().hasText()) {
                processTextRequest(update);
            } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
                processButtonPush(update);
            }
        } catch (TelegramApiException e) {
            log.error("Exception while telegram receiving and processing message. {}", getStackTrace(e));
        }
    }

    private void processButtonPush(Update update) throws TelegramApiException {
        SendMessage message = new SendMessage();
        telegramUtils.addUser(update, update.getCallbackQuery().getFrom().getId());
        message.setChatId(update.getCallbackQuery().getFrom().getId());
        String callback = update.getCallbackQuery().getData();
        if(callback.equalsIgnoreCase(START.getPath())) {
            message.setText(telegramStepsOptionsMessage);
            message.setReplyMarkup(telegramUtils.getOptionsButtons());
        } else if (callback.equalsIgnoreCase(DISHES.getPath())) {
            telegramUtils.updateTelegramUserMap(null, SLOVOTBIRATOR_REQUEST, TELEGRAM_USERS_MAP.get(update.getCallbackQuery().getFrom().getId()));
            message.setText(telegramStepsOptionsDishesMessage);
        } else if (callback.equalsIgnoreCase(OPTION1.getPath())) {
            message.setText("не нажимай");
        } else if (callback.equalsIgnoreCase(OPTION2.getPath())) {
            message.setText("не нажимай сюда блять");
        } else {
            message.setText("пиздец");
        }
        execute(message);
    }

    private void processTextRequest(Update update) throws TelegramApiException {
        SendMessage message = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        message.setChatId(chatId);
        if (update.getMessage().getText().equals("/start")) {
            execute(telegramUtils.startBot(update));
        } else {
            telegramUtils.addUser(update, update.getMessage().getChatId());
            processUserMainRequest(update, message);
            sendStatistics(update.getMessage().getFrom().getFirstName(), update.getMessage().getText(), message.getText());
            execute(message);
        }
    }

    private void processUserMainRequest(Update update, SendMessage message) {
        Long chatId = update.getMessage().getChatId();
        if(TELEGRAM_USERS_MAP.get(chatId).getCurrentCommunicationStep().equals(SLOVOTBIRATOR_REQUEST)) {
            gigachatClientService.askGigachatSlovotbirator(TELEGRAM_USERS_MAP.get(chatId));
        }
        if (TELEGRAM_USERS_MAP.get(chatId).getCurrentCommunicationStep().equals(DISHES_REQUEST)) {
            gigachatClientService.askGigachatDishes(TELEGRAM_USERS_MAP.get(chatId));
        }
        telegramUtils.defineMessageText(TELEGRAM_USERS_MAP.get(chatId), message);
    }

    private void sendStatistics(String userName, String request, String response) throws TelegramApiException {
        SendMessage requestMessage = new SendMessage();
        requestMessage.setChatId(webTelegramAdminChatId);
        requestMessage.setText(userName + " - REQUEST: " + request + "\n" + "RESPONSE: " + response);
        execute(requestMessage);
        TELEGRAM_USERS_MAP.values().forEach(System.out::println);
    }

    @Override
    public String getBotUsername() {
        return webTelegramBotUsername;
    }

    public String getBotToken() {
        return telegramBotToken;
    }
}
