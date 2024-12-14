package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.web.clients.GigachatClient;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

import java.io.IOException;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClientService {

    private final GigachatClient gigachatClient;
    private final GigachatAuthService gigachatAuthService;

    public void askGigachatQuestion() throws IOException {
        gigachatAuthService.updateAuthKey();
        ResponseDto responseDto = gigachatClient.askGigachatQuestion();
    }

    public String askGigachatQuestionFromTelegram(String question) {
        try {
            gigachatAuthService.updateAuthKey();
            System.out.println("GIGA REQUEST " + question);
            ResponseDto responseDto = gigachatClient.askGigachatQuestion(question);
            System.out.println("GIGA REPLY " + responseDto.getChoices().get(0).getMessage().getContent());
            return responseDto.getChoices().get(0).getMessage().getContent();
        } catch (IOException e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
            return "Чет я хз че ответить чел";
        }
    }
}
