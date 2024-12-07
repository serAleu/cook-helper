package ru.seraleu.gigachat.web.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResponseMessageDto {
    @JsonProperty("content")
    private String content;
    @JsonProperty("role")
    private String role;
}
