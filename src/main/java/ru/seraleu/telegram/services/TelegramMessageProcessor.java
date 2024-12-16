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

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Slf4j
@RequiredArgsConstructor
@Service("telegramMessageProcessor")
public class TelegramMessageProcessor extends TelegramLongPollingBot {

    private final String telegramBotToken;
    private final GigachatClientService gigachatClientService;

    @Value("${web.telegram.bot-username}")
    private String webTelegramBotUsername;
    @Value("${web.telegram.admin-chat-id}")
    private Long webTelegramAdminChatId;

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
                String response = "no response";
                long chatId = update.getMessage().getChatId();
                String requestText = update.getMessage().getText();
                String memberName = update.getMessage().getFrom().getFirstName();
                if (requestText.equals("/start")) {
                    startBot(chatId, memberName);
                } else {
                    System.out.println("GIGA REQUEST " + requestText);
                    response = gigachatClientService.askGigachatQuestion(requestText);
                    response = memberName + ", вот, что я для тебя нашел: " + response;
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(response);
                    execute(message);
                }
                sendStatistics(requestText, response);
            }
        } catch (TelegramApiException e) {
            log.error("Exception while telegram receiving and processing message. {}", getStackTrace(e));
        }
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

        try {
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
