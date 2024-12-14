package ru.seraleu.gigachat.web.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.seraleu.gigachat.utils.GigachatAuthContext;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.requests.RequestMessageDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

import java.util.Collections;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClient {

    private final RestTemplate gigachatClientRestTemplate;
    private final ObjectMapper mapper;
    private GigachatAuthContext authContext;
    @Value("${web.gigachat.client.uri}")
    private String webGigachatClientUri;
    @Value("${web.gigachat.client.request.role}")
    private String webGigachatClientRequestRole;
    @Value("${web.gigachat.client.request.model}")
    private String webGigachatClientRequestModel;
    @Value("${web.gigachat.client.request.content}")
    private String webGigachatClientRequestContent;

    public ResponseDto askGigachatQuestion() {
        String response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + GigachatAuthContext.accessToken);
            HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(createRequest(webGigachatClientRequestContent)), headers);
            response = gigachatClientRestTemplate.postForObject(webGigachatClientUri, request, String.class);
            return mapper.readValue(response, ResponseDto.class);
        } catch (Exception e) {
            log.error("Error while getting Giga auth key. response = {}, stackTrace: {}", response, getStackTrace(e));
            return null;
        }
    }

    public ResponseDto askGigachatQuestion(String question) {
        String response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + GigachatAuthContext.accessToken);
            HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(createRequest(question)), headers);
            response = gigachatClientRestTemplate.postForObject(webGigachatClientUri, request, String.class);
            return mapper.readValue(response, ResponseDto.class);
        } catch (Exception e) {
            log.error("Error while getting Giga auth key. response = {}, stackTrace: {}", response, getStackTrace(e));
            return null;
        }
    }

    private RequestDto createRequest(String question) {
        return new RequestDto()
                .setMaxTokens(512)
                .setN(1)
                .setStream(false)
                .setModel(webGigachatClientRequestModel)
                .setRepetitionPenalty(1)
                .setUpdateInterval(0)
                .setMessages(Collections.singletonList(new RequestMessageDto()
                        .setRole(webGigachatClientRequestRole)
                        .setContent(question)));
    }
}
