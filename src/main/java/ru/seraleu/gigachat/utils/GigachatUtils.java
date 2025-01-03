package ru.seraleu.gigachat.utils;

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
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Slf4j
@Component
@RequiredArgsConstructor
public class GigachatUtils {

    private final ObjectMapper mapper;
    private final Map<String, List<String>> gigaResponseStatusesMap;

    @Value("${gigachat.web.request.role}")
    private String gigachatWebRequestRole;
    @Value("${gigachat.web.rerequest.role}")
    private String gigachatWebRerequestRole;
    @Value("${gigachat.web.request.role}")
    private String gigachatWebRerequestContent;
    @Value("${gigachat.web.request.model}")
    private String gigachatWebRequestModel;

    public String removeGigaStatusFormResponse(ResponseDto responseDto) {
        AtomicReference<String> updatedResponse = new AtomicReference<>(responseDto.getChoices().get(0).getMessage().getContent());
        gigaResponseStatusesMap.values().forEach(statusList -> statusList.forEach(status -> updatedResponse.set(StringUtils.replaceIgnoreCase(updatedResponse.get(), status, ""))));
        return updatedResponse.get();
    }

    public RequestDto createGigaRequest(String userQuestion, String path, String baseRequest) {
        RequestDto requestDto = getRequestDtoWithFakeStory(path);
        String content = (baseRequest + userQuestion).replaceAll("\n","");
        requestDto.getMessages().add(new RequestMessageDto().setRole(gigachatWebRequestRole).setContent(content));
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

    public boolean isResponseContainGigachatStatus(ResponseDto responseDto, String status) {
        AtomicBoolean isContain = new AtomicBoolean(false);
        String response = responseDto.getChoices().get(0).getMessage().getContent();
        gigaResponseStatusesMap.get(status).forEach(gigaStatus -> {
            if(StringUtils.containsIgnoreCase(response, gigaStatus)) {
                isContain.set(true);
            }
        });
        return isContain.get();
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
