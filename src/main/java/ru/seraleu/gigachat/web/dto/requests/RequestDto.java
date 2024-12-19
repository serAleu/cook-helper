package ru.seraleu.gigachat.web.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RequestDto {
    @JsonProperty("model")
    private String model;
    @JsonProperty("messages")
    private List<RequestMessageDto> messages;
//    @JsonProperty("n")
//    private int n;
//    @JsonProperty("stream")
//    private boolean stream;
//    @JsonProperty("max_tokens")
//    private int maxTokens;
//    @JsonProperty("repetition_penalty")
//    private int repetitionPenalty;
//    @JsonProperty("update_interval")
//    private int updateInterval;
}
