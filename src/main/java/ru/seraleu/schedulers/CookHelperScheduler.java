package ru.seraleu.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.seraleu.gigachat.services.GigachatAuthService;
import ru.seraleu.gigachat.services.GigachatClientService;
import ru.seraleu.gigachat.web.clients.GigachatAuthClient;
import ru.seraleu.gigachat.web.clients.GigachatClient;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CookHelperScheduler {

    private final GigachatAuthService gigachatAuthService;
    private final GigachatClientService gigachatClientService;

    @Scheduled(fixedDelay = 10000)
    public void collGigachatAuth() throws IOException {
        gigachatClientService.askGigachatQuestion();
    }
}
