package ru.seraleu.gigachat.web.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.seraleu.gigachat.utils.GigachatAuthContext;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatAuthClient {

    private final RestTemplate gigachatAuthRestTemplate;
    private final ObjectMapper mapper;
    @Value("${web.gigachat.auth.payload}")
    private String webGigachatAuthPayload;
    @Value("${web.gigachat.auth.uri}")
    private String webGigachatAuthUri;

    public String getGigachatAuthKey() {
        String responseJson = null;
        try {
            responseJson = gigachatAuthRestTemplate.postForObject(webGigachatAuthUri, webGigachatAuthPayload, String.class);
            System.out.println("AUTH RESPONSE " + responseJson);
            JsonNode jsonNode = mapper.readTree(responseJson);
            GigachatAuthContext.accessToken = jsonNode.get("access_toke").asText();
            GigachatAuthContext.expiresAt = jsonNode.get("expires_at").asLong();
        } catch (Exception e) {
            log.error("Error while getting Giga auth key. response = {}, stackTrace: {}", responseJson, e.getStackTrace());
        }
        return responseJson;
    }
}
