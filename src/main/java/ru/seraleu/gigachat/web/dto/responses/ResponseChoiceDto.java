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
public class ResponseChoiceDto {
    @JsonProperty("message")
    private ResponseMessageDto message;
    @JsonProperty("index")
    private Integer index;
    @JsonProperty("finishReason")
    private String finish_reason;
}
