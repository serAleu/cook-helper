package ru.seraleu.gigachat.web.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RequestMessageDto {
    @JsonProperty("role")
    private String role;
    @JsonProperty("content")
    private String content;
}
