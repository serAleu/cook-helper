package ru.seraleu.telegram.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.seraleu.gigachat.services.GigachatClientService;
import ru.seraleu.telegram.users.TelegramRequestType;
import ru.seraleu.telegram.users.TelegramUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.users.TelegramRequestType.*;
import static ru.seraleu.telegram.users.TelegramResponseType.*;

@Slf4j
@RequiredArgsConstructor
@Service("telegramMessageProcessor")
public class TelegramMessageProcessor extends TelegramLongPollingBot {

    private final String telegramBotToken;
    private final GigachatClientService gigachatClientService;
    private final List<String> swearings;

    @Value("${telegram.web.bot-username}")
    private String webTelegramBotUsername;
    @Value("${telegram.web.admin-chat-id}")
    private Long webTelegramAdminChatId;
    @Value("${telegram.swearing.reply}")
    private String telegramSwearingReply;

    private final Map<Long, String> oneMessageFromChat = new ConcurrentHashMap<>();

    public void sendMessage(String str, Long webTelegramAdminChatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(webTelegramAdminChatId);
        sendMessage.setText(str);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception while telegram message sending. {}", getStackTrace(e));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                SendMessage message = new SendMessage();
                TelegramUser user = getTelegramUser(update);
                if(isRequestContainSwearing(user.getRequestsMap().get(INITIAL_REQUEST))) {
                    message.setText(user.getResponsesMap().get(SWEARING_RESPONSE));
                } else {
                    if (user.getRequestsMap().get(INITIAL_REQUEST).equals("/start")) {
                        startBot(user.getChatId(), user.getUserName());
                    } else {
                        user = gigachatClientService.askGigachatForGettingProductList(user);
                        if(user.getRequestsMap().containsKey(LIST_OF_DISHES_REQUEST)) {
                            user = gigachatClientService.askGigachatQuestion(user);
                            //вот здесь отдельный метод где юзеру отправляется та инфа которая получилсас
                            message.setText(user.getUserName() + ", вот, что я для тебя нашел: " + user.getRequestsMap().get(LIST_OF_DISHES_REQUEST));
                        }
                    }
                }
                message.setChatId(user.getChatId());
                execute(message);
                sendStatistics(user.getRequestsMap().get(INITIAL_REQUEST), message.getText());
            }
        } catch (TelegramApiException e) {
            log.error("Exception while telegram receiving and processing message. {}", getStackTrace(e));
        }
    }

    private TelegramUser getTelegramUser(Update update) {
        return new TelegramUser()
                .setUserName(update.getMessage().getFrom().getFirstName())
                .setChatId(update.getMessage().getChatId())
                .setRequestsMap(new HashMap<>() {{
                    put(INITIAL_REQUEST, update.getMessage().getText());
                    put(LIST_OF_PRODUCT_REQUEST, update.getMessage().getText());
                }})
                .setResponsesMap(new HashMap<>() {{
                    put(NO_RESPONSE, "no response");
                    put(SWEARING_RESPONSE, telegramSwearingReply);
                }});
    }

    private boolean isRequestContainSwearing(String requestText) {
        AtomicBoolean isContain = new AtomicBoolean(false);
        swearings.forEach(swearing -> {
            if(StringUtils.containsIgnoreCase(requestText, swearing)) {
                isContain.set(true);
            }
        });
        return isContain.get();
    }

    private void sendStatistics(String request, String response) throws TelegramApiException {
        SendMessage requestMessage = new SendMessage();
        requestMessage.setChatId(webTelegramAdminChatId);
        requestMessage.setText("REQUEST: " + request + "\n" + "RESPONSE: " + response);
        execute(requestMessage);
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет, " + userName + "! Я - Нейро-Мега-Супер-Шеф-Повар. Я - Бот, который поможет тебе приготовить все, " +
                "что угодно из любых съедобных ингредиентов, которые ты сможешь найти у себя в холодильнике (или в любом " +
                "другом месте где у тебя может заваляться что нибудь интересное). Просто напиши мне свои ингредиенты в свободной " +
                "форме и предложу тебе несколько блюд, которые ты сможешь из них приготовить!");

        InputStream stream;
        try {
            stream = Files.newInputStream(Path.of("src/main/resources/telegram/start_image.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(stream, "src/main/resources/telegram/start_image.jpg"));

        try {
            execute(sendPhoto);
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return webTelegramBotUsername;
    }

    public String getBotToken() {
        return telegramBotToken;
    }
}
