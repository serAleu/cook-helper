package ru.seraleu.gigachat.web.dto.responses;

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
public class ResponseDto {
    @JsonProperty("choices")
    private List<ResponseChoiceDto> choices;
    @JsonProperty("created")
    private Integer created;
    @JsonProperty("model")
    private String model;
    @JsonProperty("object")
    private String object;
    @JsonProperty("usage")
    private ResponseUsageDto usage;
}
