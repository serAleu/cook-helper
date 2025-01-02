package ru.seraleu.gigachat.web.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.seraleu.gigachat.utils.GigachatAuthContext;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClient {

    private final RestTemplate gigachatClientRestTemplate;
    private final ObjectMapper mapper;
    @Value("${gigachat.web.client.uri}")
    private String gigachatWebClientUri;

    public ResponseDto askGigachatQuestion(RequestDto requestDto, String xSessionId) {
        String response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + GigachatAuthContext.accessToken);
            headers.set("X-Session-ID", xSessionId);
            String requestJson = mapper.writeValueAsString(requestDto);
            log.info("GIGA REQUEST JSON: {}", requestJson);
            HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
            response = gigachatClientRestTemplate.postForObject(gigachatWebClientUri, request, String.class);
            log.info("GIGA REPLY JSON: {}", response);
            if(!StringUtils.isBlank(response)) {
                return mapper.readValue(response, ResponseDto.class);
            } else {
                log.info("Error while getting Giga response: is empty. response = {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("Error while getting Giga response. response = {}, stackTrace: {}", response, getStackTrace(e));
            return null;
        }
    }
}
