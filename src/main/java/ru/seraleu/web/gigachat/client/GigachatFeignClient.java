package ru.seraleu.web.gigachat.client;

import feign.Headers;
import feign.RequestLine;
import org.springframework.http.MediaType;

public interface GigachatFeignClient {

    @RequestLine("POST /hotel/report")
    @Headers({"Content-Type: " + MediaType.APPLICATION_JSON_VALUE})
    String getAccessToken(String request);
}
