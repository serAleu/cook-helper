package ru.seraleu.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.seraleu.gigachat.services.GigachatClientService;

@Component
@RequiredArgsConstructor
@Profile("!telegram")
public class CookHelperScheduler {

    private final GigachatClientService gigachatClientService;
    private final static String REQUEST  = "картошка, маврошка, хреношка, петрушка, ватрушка, плюшка, суп грибной, рыбий хвост";

    @Scheduled(fixedDelay = 10000)
    public void callGigachat() {
        gigachatClientService.askGigachatQuestion(REQUEST);
    }
}
