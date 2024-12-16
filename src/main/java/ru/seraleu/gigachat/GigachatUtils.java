package ru.seraleu.gigachat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.requests.RequestMessageDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class GigachatUtils {

    @Value("${web.gigachat.fake-story.dishes-list.1121.user.role}")
    private String webGigachatFakeStoryDishesList1121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.1121.user.content}")
    private String webGigachatFakeStoryDishesList1121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.1121.assistant.role}")
    private String webGigachatFakeStoryDishesList1121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.1121.assistant.content}")
    private String webGigachatFakeStoryDishesList1121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.2121.user.role}")
    private String webGigachatFakeStoryDishesList2121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.2121.user.content}")
    private String webGigachatFakeStoryDishesList2121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.2121.assistant.role}")
    private String webGigachatFakeStoryDishesList2121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.2121.assistant.content}")
    private String webGigachatFakeStoryDishesList2121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.3121.user.role}")
    private String webGigachatFakeStoryDishesList3121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.3121.user.content}")
    private String webGigachatFakeStoryDishesList3121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.3121.assistant.role}")
    private String webGigachatFakeStoryDishesList3121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.3121.assistant.content}")
    private String webGigachatFakeStoryDishesList3121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.4121.user.role}")
    private String webGigachatFakeStoryDishesList4121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.4121.user.content}")
    private String webGigachatFakeStoryDishesList4121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.4121.assistant.role}")
    private String webGigachatFakeStoryDishesList4121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.4121.assistant.content}")
    private String webGigachatFakeStoryDishesList4121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.5121.user.role}")
    private String webGigachatFakeStoryDishesList5121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.5121.user.content}")
    private String webGigachatFakeStoryDishesList5121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.5121.assistant.role}")
    private String webGigachatFakeStoryDishesList5121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.5121.assistant.content}")
    private String webGigachatFakeStoryDishesList5121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.6121.user.role}")
    private String webGigachatFakeStoryDishesList6121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.6121.user.content}")
    private String webGigachatFakeStoryDishesList6121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.6121.assistant.role}")
    private String webGigachatFakeStoryDishesList6121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.6121.assistant.content}")
    private String webGigachatFakeStoryDishesList6121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.7121.user.role}")
    private String webGigachatFakeStoryDishesList7121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.7121.user.content}")
    private String webGigachatFakeStoryDishesList7121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.7121.assistant.role}")
    private String webGigachatFakeStoryDishesList7121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.7121.assistant.content}")
    private String webGigachatFakeStoryDishesList7121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.8121.user.role}")
    private String webGigachatFakeStoryDishesList8121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.8121.user.content}")
    private String webGigachatFakeStoryDishesList8121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.8121.assistant.role}")
    private String webGigachatFakeStoryDishesList8121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.8121.assistant.content}")
    private String webGigachatFakeStoryDishesList8121AssistantContent;
    @Value("${web.gigachat.fake-story.dishes-list.8121.user.role}")
    private String webGigachatFakeStoryDishesList9121UserRole;
    @Value("${web.gigachat.fake-story.dishes-list.9121.user.content}")
    private String webGigachatFakeStoryDishesList9121UserContent;
    @Value("${web.gigachat.fake-story.dishes-list.9121.assistant.role}")
    private String webGigachatFakeStoryDishesList9121AssistantRole;
    @Value("${web.gigachat.fake-story.dishes-list.9121.assistant.content}")
    private String webGigachatFakeStoryDishesList9121AssistantContent;
    @Value("${web.gigachat.request.role}")
    private String webGigachaRequestRole;
    @Value("${web.gigachat.request.content}")
    private String webGigachatRequestContent;
    @Value("${web.gigachat.rerequest.role}")
    private String webGigachaRerequestRole;
    @Value("${web.gigachat.request.role}")
    private String webGigachaRerequestContent;
    @Value("${web.gigachat.request.model}")
    private String webGigachatRequestModel;
    @Value("${web.gigachat.status.success}")
    private String webGigachatStatusSuccess;
    @Value("${web.gigachat.status.failure}")
    private String webGigachatStatusFailure;

    public RequestDto createGigaRequestForDishesListGetting(String question) {
        List<RequestMessageDto> messages = new ArrayList<>();
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList1121UserRole).setContent(webGigachatFakeStoryDishesList1121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList1121AssistantRole).setContent(webGigachatFakeStoryDishesList1121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList2121UserRole).setContent(webGigachatFakeStoryDishesList2121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList2121AssistantRole).setContent(webGigachatFakeStoryDishesList2121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList3121UserRole).setContent(webGigachatFakeStoryDishesList3121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList3121AssistantRole).setContent(webGigachatFakeStoryDishesList3121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList4121UserRole).setContent(webGigachatFakeStoryDishesList4121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList4121AssistantRole).setContent(webGigachatFakeStoryDishesList4121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList5121UserRole).setContent(webGigachatFakeStoryDishesList5121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList5121AssistantRole).setContent(webGigachatFakeStoryDishesList5121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList6121UserRole).setContent(webGigachatFakeStoryDishesList6121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList6121AssistantRole).setContent(webGigachatFakeStoryDishesList6121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList7121UserRole).setContent(webGigachatFakeStoryDishesList7121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList7121AssistantRole).setContent(webGigachatFakeStoryDishesList7121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList8121UserRole).setContent(webGigachatFakeStoryDishesList8121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList8121AssistantRole).setContent(webGigachatFakeStoryDishesList8121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList9121UserRole).setContent(webGigachatFakeStoryDishesList9121UserContent));
        messages.add(new RequestMessageDto().setRole(webGigachatFakeStoryDishesList9121AssistantRole).setContent(webGigachatFakeStoryDishesList9121AssistantContent));
        messages.add(new RequestMessageDto().setRole(webGigachaRequestRole).setContent(webGigachatRequestContent + " " + question));
        return new RequestDto()
                .setMaxTokens(512)
                .setN(1)
                .setStream(false)
                .setModel(webGigachatRequestModel)
                .setRepetitionPenalty(1)
                .setUpdateInterval(0)
                .setMessages(messages);
    }

    public RequestDto updateGigaRequestForRerequestDishesListGetting(RequestDto requestDto, String question) {
        List<RequestMessageDto> messages = requestDto.getMessages();
        messages.add(new RequestMessageDto().setRole(webGigachaRerequestRole).setContent(webGigachaRerequestContent + " " + question));
        requestDto.setMessages(messages);
        return requestDto;
    }


    public boolean isValidResponse(ResponseDto responseDto) {
        return responseDto != null && responseDto.getChoices() != null && !responseDto.getChoices().isEmpty()
                && responseDto.getChoices().get(0) != null && responseDto.getChoices().get(0).getMessage() != null
                && responseDto.getChoices().get(0).getMessage().getContent() != null;
    }

    public boolean isResponseNotStartWithGigachatStatus(ResponseDto responseDto) {
        String response = responseDto.getChoices().get(0).getMessage().getContent();
        return !StringUtils.startsWithIgnoreCase(response, webGigachatStatusSuccess)
                && !StringUtils.startsWithIgnoreCase(response, webGigachatStatusFailure);
    }

    public String prepareGigaResponseForUser(ResponseDto responseDto) {
        String response = responseDto.getChoices().get(0).getMessage().getContent();
        response = StringUtils.replaceIgnoreCase(response, webGigachatStatusSuccess, "");
        response = StringUtils.replaceIgnoreCase(response, webGigachatStatusFailure, "");
        return response;
    }
}
