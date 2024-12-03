package ru.seraleu.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.seraleu.web.gigachat.client.GigachatAuthFeignClient;

@Component
@RequiredArgsConstructor
public class CookHelperScheduler {

//    @Value("${web.feign.gigachat.auth.payload}")
//    private String webFeignGigachatAuthPayload;

    private final GigachatAuthFeignClient gigachatAuthFeignClient;

    @Scheduled(fixedDelay = 100000)
    public void collGigachatAuth() {
        String token = gigachatAuthFeignClient.getAccessToken("scope=GIGACHAT_API_PERS");
        System.out.println(token);
    }
}
