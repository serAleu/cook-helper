package ru.seraleu.gigachat.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class GigachatAuthContext {
    @JsonProperty("access_token")
    public static String accessToken;
    @JsonProperty("expires_at")
    public static Long expiresAt;
}
