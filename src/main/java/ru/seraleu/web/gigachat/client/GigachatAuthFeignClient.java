package ru.seraleu.web.gigachat.client;

import feign.Headers;
import feign.RequestLine;
import org.springframework.http.MediaType;

public interface GigachatAuthFeignClient {

    @RequestLine("POST /api/v2/oauth")
    @Headers({"Content-Type: " + MediaType.APPLICATION_JSON_VALUE})
    String getAccessToken(String payload);
}
