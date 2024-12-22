package ru.seraleu.gigachat.web.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.gigachat.utils.GigachatAuthContext.setAccessTokenAndExpiresAt;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatAuthClient {

    private final RestTemplate gigachatAuthRestTemplate;
    private final ObjectMapper mapper;
    @Value("${gigachat.web.auth.payload}")
    private String gigachatWebAuthPayload;
    @Value("${gigachat.web.auth.uri}")
    private String gigachatWebAuthUri;

    public String getGigachatAuthKey() {
        String responseJson = null;
        try {
            responseJson = gigachatAuthRestTemplate.postForObject(gigachatWebAuthUri, gigachatWebAuthPayload, String.class);
            setAccessTokenAndExpiresAt(mapper, responseJson);
            System.out.println("AUTH RESPONSE " + responseJson);
        } catch (Exception e) {
            log.error("Error while getting Giga auth key. response = {}, stackTrace: {}", responseJson, getStackTrace(e));
        }
        return responseJson;
    }
}
