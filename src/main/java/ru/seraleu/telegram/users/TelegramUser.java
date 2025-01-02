package ru.seraleu.telegram.users;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TelegramUser {

    private Long chatId;
    private String userName;
    private Map<TelegramCommunicationStep, String> userCommunications;

}