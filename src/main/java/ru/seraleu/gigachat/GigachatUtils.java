package ru.seraleu.gigachat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.requests.RequestMessageDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

import java.io.*;

import java.util.List;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Slf4j
@Component
@RequiredArgsConstructor
public class GigachatUtils {

    private final ObjectMapper mapper;

    @Value("${gigachat.web.request.role}")
    private String gigachatWebRequestRole;
    @Value("${gigachat.web.request.content}")
    private String gigachatWebRequestContent;
    @Value("${gigachat.web.rerequest.role}")
    private String gigachatWebRerequestRole;
    @Value("${gigachat.web.request.role}")
    private String gigachatWebRerequestContent;
    @Value("${gigachat.web.request.model}")
    private String gigachatWebRequestModel;
    @Value("${gigachat.web.status.success}")
    private String gigachatWebStatusSuccess;
    @Value("${gigachat.web.status.failure}")
    private String gigachatWebStatusFailure;
    @Value("${gigachat.web.fake-message.behaviour.path}")
    private String gigachatWebFakeMessageBehaviourPath;
    @Value("${gigachat.web.fake-message.slovotbirator.path}")
    private String gigachatWebFakeMessageSlovotbiratorPath;
    @Value("${gigachat.web.fake-message.slovotbirator.request}")
    private String gigachatWebFakeMessageSlovotbiratorRequest;

    public RequestDto createGigaRequestForSlovotbiratorCalling(String question) {
        RequestDto requestDto = getRequestDtoWithFakeStory(gigachatWebFakeMessageSlovotbiratorPath);
        requestDto.getMessages().add(new RequestMessageDto().setRole(gigachatWebRequestRole).setContent(gigachatWebFakeMessageSlovotbiratorRequest + question));
        return requestDto
                .setMaxTokens(512)
                .setN(1)
                .setStream(false)
                .setModel(gigachatWebRequestModel)
                .setRepetitionPenalty(1)
                .setUpdateInterval(0);
    }

    public RequestDto createGigaRequestForDishesListGetting(String question) {
        RequestDto requestDto = getRequestDtoWithFakeStory(gigachatWebFakeMessageBehaviourPath);
        requestDto.getMessages().add(new RequestMessageDto().setRole(gigachatWebRequestRole).setContent(gigachatWebRequestContent + question));
        return requestDto
                .setMaxTokens(512)
                .setN(1)
                .setStream(false)
                .setModel(gigachatWebRequestModel)
                .setRepetitionPenalty(1)
                .setUpdateInterval(0);
    }

    public RequestDto updateGigaRequestForRerequestDishesListGetting(RequestDto requestDto, String question) {
        List<RequestMessageDto> messages = requestDto.getMessages();
        messages.add(new RequestMessageDto().setRole(gigachatWebRerequestRole).setContent(gigachatWebRerequestContent + question));
        requestDto.setMessages(messages);
        return requestDto;
    }


    public boolean isValidResponse(ResponseDto responseDto) {
        return responseDto != null && responseDto.getChoices() != null && !responseDto.getChoices().isEmpty()
                && responseDto.getChoices().get(0) != null && responseDto.getChoices().get(0).getMessage() != null
                && responseDto.getChoices().get(0).getMessage().getContent() != null;
    }

    public boolean isResponseNotContainGigachatStatus(ResponseDto responseDto) {
        String response = responseDto.getChoices().get(0).getMessage().getContent();
        return !StringUtils.containsIgnoreCase(response, gigachatWebStatusSuccess)
                && !StringUtils.containsIgnoreCase(response, gigachatWebStatusFailure);
    }

    public String prepareGigaResponseForUser(ResponseDto responseDto) {
        String response = responseDto.getChoices().get(0).getMessage().getContent();
        response = StringUtils.replaceIgnoreCase(response, gigachatWebStatusSuccess, "");
        response = StringUtils.replaceIgnoreCase(response, gigachatWebStatusFailure, "");
        return response;
    }

    public String parseGigaProductListResponseToString(ResponseDto responseDto) {
        return responseDto.getChoices().get(0).getMessage().getContent();
    }

    private RequestDto getRequestDtoWithFakeStory(String filePath) {
        RequestDto request = new RequestDto();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            StringBuilder fakeStory = new StringBuilder();
            while (reader.ready()) {
                fakeStory.append(reader.readLine());
            }
            request = mapper.reader().forType(RequestDto.class).readValue(String.valueOf(fakeStory));
        } catch (Exception e) {
            log.error("Error while fake story reading. {}", getStackTrace(e));
        }
        return request;
    }
}
