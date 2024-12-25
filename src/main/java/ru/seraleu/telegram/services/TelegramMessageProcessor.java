package ru.seraleu.telegram.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.seraleu.gigachat.services.GigachatClientService;
import ru.seraleu.telegram.users.TelegramUser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.users.TelegramRequestType.LIST_OF_DISHES_REQUEST;

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
    @Value("${telegram.swearing.reply}")
    private String telegramSwearingReply;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getChatId() != null) {
                Long chatId = update.getMessage().getChatId();
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                if (update.getMessage().getText().equals("/start")) {
                    execute(telegramUtils.startBot(update));
                } else {
                    if (telegramUtils.isRequestContainSwearing(update.getMessage().getText())) {
                        message.setText(telegramSwearingReply);
                    } else {
                        processUserRequest(update, message);
                    }
                    execute(message);
                    sendStatistics(update.getMessage().getFrom().getFirstName(), update.getMessage().getText(), message.getText());
                }
            }
        } catch (TelegramApiException e) {
            log.error("Exception while telegram receiving and processing message. {}", getStackTrace(e));
        }
    }

    private void processUserRequest(Update update, SendMessage message) {
        Long chatId = update.getMessage().getChatId();
        telegramUtils.updateTelegramUserMap(update);
        TelegramUser user = gigachatClientService.askGigachatForGettingProductList(TELEGRAM_USERS_MAP.get(chatId));
        TELEGRAM_USERS_MAP.put(chatId, user);
        if (TELEGRAM_USERS_MAP.get(chatId).getRequestsMap().containsKey(LIST_OF_DISHES_REQUEST)) {
            user = gigachatClientService.askGigachatQuestion(TELEGRAM_USERS_MAP.get(chatId));
            TELEGRAM_USERS_MAP.put(chatId, user);
            //вот здесь отдельный метод где юзеру отправляется та инфа которая получилсас
            message.setText(TELEGRAM_USERS_MAP.get(chatId).getUserName() + ", вот, что я для тебя нашел: " + TELEGRAM_USERS_MAP.get(chatId).getRequestsMap().get(LIST_OF_DISHES_REQUEST));
        }
    }

    private void sendStatistics(String userName, String request, String response) throws TelegramApiException {
        SendMessage requestMessage = new SendMessage();
        requestMessage.setChatId(webTelegramAdminChatId);
        requestMessage.setText(userName + " - REQUEST: " + request + "\n" + "RESPONSE: " + response);
        execute(requestMessage);
    }

    @Override
    public String getBotUsername() {
        return webTelegramBotUsername;
    }

    public String getBotToken() {
        return telegramBotToken;
    }
}
