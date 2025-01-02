package ru.seraleu.gigachat.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Slf4j
@Profile("giga")
@Component
@RequiredArgsConstructor
public class GigachatAuthContext {

    private final ObjectMapper mapper;
    private static ObjectMapper objectMapper;
    @Value("${gigachat.web.auth.last-auth-key-path}")
    private String gigachatWebAuthLastAuthKeyPath;
    @JsonProperty("access_token")
    public static String accessToken;
    @JsonProperty("expires_at")
    public static Long expiresAt;

    @PostConstruct
    public void init() throws IOException {
        objectMapper = mapper;
        File file = new File(gigachatWebAuthLastAuthKeyPath);
        if(new File(gigachatWebAuthLastAuthKeyPath).isFile()) {
            String lastAuthResponseJson = Files.readString(file.toPath(), Charset.defaultCharset());
            if(!StringUtils.isBlank(lastAuthResponseJson)) {
                try {
                    setAccessTokenAndExpiresAt(lastAuthResponseJson);
                } catch (Exception e) {
                    log.error("Exception while getting Gigachat last auth context from the file. last_response_json = {}, exception = {}", lastAuthResponseJson, getStackTrace(e));
                }
            }
        }
    }

    public static void setAccessTokenAndExpiresAt(String responseJson) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(responseJson);
        GigachatAuthContext.accessToken = jsonNode.get("access_token").asText();
        GigachatAuthContext.expiresAt = jsonNode.get("expires_at").asLong();
    }
}
