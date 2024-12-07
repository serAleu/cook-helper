package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.web.clients.GigachatClient;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClientService {

    private final GigachatClient gigachatClient;

    public void askGigachatQuestion() {
        ResponseDto responseDto = gigachatClient.askGigachatQuestion();
        System.out.println(responseDto);
        System.out.println("==============");
    }
}
