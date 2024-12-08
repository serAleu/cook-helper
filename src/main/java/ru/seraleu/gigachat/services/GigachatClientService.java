package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.web.clients.GigachatClient;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClientService {

    private final GigachatClient gigachatClient;
    private final GigachatAuthService gigachatAuthService;

    public void askGigachatQuestion() throws IOException {
        gigachatAuthService.updateAuthKey();
        ResponseDto responseDto = gigachatClient.askGigachatQuestion();
        System.out.println("GIGA REPLY " + responseDto.toString());
    }
}
