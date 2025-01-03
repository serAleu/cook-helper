package ru.seraleu.telegram.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.seraleu.gigachat.services.GigachatClientService;
import ru.seraleu.telegram.users.TelegramUser;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.users.TelegramCommunicationStep.*;

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

    @Override
    public void onUpdateReceived(Update update) {
        try {
            SendMessage message = new SendMessage();
            if (update.hasMessage() && update.getMessage().getChatId() != null && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                message.setChatId(chatId);
                if (update.getMessage().getText().equals("/start")) {
                    execute(telegramUtils.startBot(update));
                } else {
                    //добавь дефолтные ответы от гиги в обработку типа чел же может писать мат разными способами которые приложение пропустит а гига обидиться
                    telegramUtils.addUser(update);
                    processUserMainRequest(update, message);
                    sendStatistics(update.getMessage().getFrom().getFirstName(), update.getMessage().getText(), message.getText());
                }
                execute(message);
            } else if (update.hasCallbackQuery() && update.getCallbackQuery().getId() != null) {
                System.out.println("ПРИВ " + update.getCallbackQuery().getData());
                message.setText(update.getCallbackQuery().getData());
                message.setChatId(update.getCallbackQuery().getFrom().getId());
                execute(message);
            }
        } catch (TelegramApiException e) {
            log.error("Exception while telegram receiving and processing message. {}", getStackTrace(e));
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
