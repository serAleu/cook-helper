package ru.seraleu.telegram.users;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class TelegramUser {

    private Long chatId;
    private String userName;
    private Map<TelegramRequestType, String> requestsMap;
    private Map<TelegramResponseType, String> responsesMap;
}