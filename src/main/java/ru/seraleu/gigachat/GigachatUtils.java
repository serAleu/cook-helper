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

    @Value("${web.gigachat.request.role}")
    private String webGigachatRequestRole;
    @Value("${web.gigachat.request.content}")
    private String webGigachatRequestContent;
    @Value("${web.gigachat.rerequest.role}")
    private String webGigachatRerequestRole;
    @Value("${web.gigachat.request.role}")
    private String webGigachatRerequestContent;
    @Value("${web.gigachat.request.model}")
    private String webGigachatRequestModel;
    @Value("${web.gigachat.status.success}")
    private String webGigachatStatusSuccess;
    @Value("${web.gigachat.status.failure}")
    private String webGigachatStatusFailure;
    @Value("${web.gigachat.fake-message.behaviour}")
    private String webGigachatFakeMessageBehaviour;

    @Value("${web.gigachat.fake-message.product-list}")
    private String webGigachatFakeMessageProductList;

    public RequestDto createGigaRequestForGettingListOfProduct(String question) {
        RequestDto requestDto = getRequestDtoWithFakeStory(webGigachatFakeMessageProductList);
        requestDto.getMessages().add(new RequestMessageDto().setRole(webGigachatRequestRole).setContent(question));
        return requestDto
//                .setMaxTokens(512)
//                .setN(1)
//                .setStream(false)
                .setModel(webGigachatRequestModel)
//                .setRepetitionPenalty(1)
//                .setUpdateInterval(0)
                ;
    }

    public RequestDto createGigaRequestForDishesListGetting(String question) {
        RequestDto requestDto = getRequestDtoWithFakeStory(webGigachatFakeMessageBehaviour);
        requestDto.getMessages().add(new RequestMessageDto().setRole(webGigachatRequestRole).setContent(question));
        return requestDto
//                .setMaxTokens(512)
//                .setN(1)
//                .setStream(false)
                .setModel(webGigachatRequestModel)
//                .setRepetitionPenalty(1)
//                .setUpdateInterval(0)
                ;
    }

    public RequestDto updateGigaRequestForRerequestDishesListGetting(RequestDto requestDto, String question) {
        List<RequestMessageDto> messages = requestDto.getMessages();
        messages.add(new RequestMessageDto().setRole(webGigachatRerequestRole).setContent(webGigachatRerequestContent + " " + question));
        requestDto.setMessages(messages);
        return requestDto;
    }


    public boolean isValidResponse(ResponseDto responseDto) {
        return responseDto != null && responseDto.getChoices() != null && !responseDto.getChoices().isEmpty()
                && responseDto.getChoices().get(0) != null && responseDto.getChoices().get(0).getMessage() != null
                && responseDto.getChoices().get(0).getMessage().getContent() != null;
    }

    public boolean isResponseContainGigachatStatus(ResponseDto responseDto) {
        String response = responseDto.getChoices().get(0).getMessage().getContent();
        return !StringUtils.containsIgnoreCase(response, webGigachatStatusSuccess)
                && !StringUtils.containsIgnoreCase(response, webGigachatStatusFailure);
    }

    public String prepareGigaResponseForUser(ResponseDto responseDto) {
        String response = responseDto.getChoices().get(0).getMessage().getContent();
        response = StringUtils.replaceIgnoreCase(response, webGigachatStatusSuccess, "");
        response = StringUtils.replaceIgnoreCase(response, webGigachatStatusFailure, "");
        return response;
    }

    public String parseGigaProductListResponseToString(ResponseDto responseDto) {
        return responseDto.getChoices().get(0).getMessage().getContent();
    }

    private RequestDto getRequestDtoWithFakeStory(String filePath) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            StringBuilder fakeStory = new StringBuilder();
            while (reader.ready()) {
                fakeStory.append(reader.readLine());
            }
            return mapper.reader().forType(RequestDto.class).readValue(String.valueOf(fakeStory));
        } catch (Exception e) {
            log.error("Error while fake story reading. {}", getStackTrace(e));
        }
        return new RequestDto();
    }
}
