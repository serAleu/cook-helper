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
    @Value("${telegram.forbidden-words.reply}")
    private String telegramForbiddenWordsReply;

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
                    if (telegramUtils.isRequestContainForbiddenWord(update.getMessage().getText())) {
                        message.setText(telegramForbiddenWordsReply);
                    } else {
                        //сейчас по дефолту считается, что когда мы доходим до сюда, то инсертим в мапу словотбиратор реквест, но надо сделать маршрутизацию через кнопочки в тг
                        //добавь дефолтные ответы от гиги в обработку типа чел же может писать мат разными способами которые приложение пропустит а гига обидиться
                        telegramUtils.addNewUserToMapOrClearOldErrorStatuses(update);
                        processUserMainRequest(update, message);
                    }
                    sendStatistics(update.getMessage().getFrom().getFirstName(), update.getMessage().getText(), message.getText());
                    execute(message);
                }
            }
        } catch (TelegramApiException e) {
            log.error("Exception while telegram receiving and processing message. {}", getStackTrace(e));
        }
    }

    private void processUserMainRequest(Update update, SendMessage message) {
        Long chatId = update.getMessage().getChatId();
        gigachatClientService.askGigachatSlovotbirator(TELEGRAM_USERS_MAP.get(chatId));
        if (TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().containsKey(DISHES_REQUEST)) {
            gigachatClientService.askGigachatDishes(TELEGRAM_USERS_MAP.get(chatId));
            message.setText(TELEGRAM_USERS_MAP.get(chatId).getUserName() + ", вот, что я для тебя нашел: " + TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().get(DISHES_RESPONSE));
        } else if (TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().containsKey(NO_RESPONSE)) {
            message.setText(TELEGRAM_USERS_MAP.get(chatId).getUserName() + " " + TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().get(NO_RESPONSE));
        } else if (TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().containsKey(STUPID_USER_RESPONSE)) {
            message.setText(TELEGRAM_USERS_MAP.get(chatId).getUserName() + " " + TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().get(STUPID_USER_RESPONSE));
        } else if (TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().containsKey(STUPID_GIGA_RESPONSE)) {
            message.setText(TELEGRAM_USERS_MAP.get(chatId).getUserName() + " " + TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().get(STUPID_GIGA_RESPONSE));
        } else {
            message.setText(TELEGRAM_USERS_MAP.get(chatId).getUserName() + " " + TELEGRAM_USERS_MAP.get(chatId).getUserCommunications().get(NO_RESPONSE));
        }
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
