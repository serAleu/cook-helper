package ru.seraleu.gigachat.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.ToString;

public class GigachatAuthContext {
    @JsonProperty("access_token")
    public static String accessToken;
    @JsonProperty("expires_at")
    public static Long expiresAt;

    public static void setAccessTokenAndExpiresAt(ObjectMapper mapper, String responseJson) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(responseJson);
        GigachatAuthContext.accessToken = jsonNode.get("access_token").asText();
        GigachatAuthContext.expiresAt = jsonNode.get("expires_at").asLong();
    }
}
