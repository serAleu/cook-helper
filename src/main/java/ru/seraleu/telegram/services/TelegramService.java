package ru.seraleu.telegram.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.services.GigachatClientService;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final String telegramBotToken;
    private final GigachatClientService gigachatClientService;
    private final TelegramMessageProcessor telegramMessageProcessor;

    @Value("${web.telegram.bot-username}")
    private String webTelegramBotUsername;
    @Value("${web.telegram.admin-chat-id}")
    private Long webTelegramAdminChatId;



}
