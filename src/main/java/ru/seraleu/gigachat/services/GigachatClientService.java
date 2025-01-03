package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.utils.GigachatUtils;
import ru.seraleu.gigachat.web.clients.GigachatClient;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;
import ru.seraleu.telegram.utils.TelegramUtils;
import ru.seraleu.telegram.users.TelegramUser;

import java.io.IOException;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.users.TelegramCommunicationStep.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClientService {

    private final GigachatClient gigachatClient;
    private final GigachatUtils gigachatUtils;
    private final TelegramUtils telegramUtils;
    @Value("${gigachat.web.error-messages-for-user.stupid-giga}")
    private String gigachatWebErrorsStupidGiga;
    @Value("${gigachat.web.error-messages-for-user.stupid-user}")
    private String gigachatWebErrorsStupidUser;
    @Value("${gigachat.web.error-messages-for-user.incorrect-request}")
    private String gigachatWebErrorsIncorrectRequest;
    @Value("${gigachat.web.fake-message.slovotbirator.xSessionId}")
    private String gigachatWebFakeMessageSlovotbiratorXSessionId;
    @Value("${gigachat.web.fake-message.behaviour.xSessionId}")
    private String gigachatWebFakeMessageBehaviourXSessionId;
    @Value("${gigachat.web.fake-message.slovotbirator.path}")
    private String gigachatWebFakeMessageSlovotbiratorPath;
    @Value("${gigachat.web.fake-message.slovotbirator.request}")
    private String gigachatWebFakeMessageSlovotbiratorRequest;
    @Value("${gigachat.web.fake-message.behaviour.path}")
    private String gigachatWebFakeMessageBehaviourPath;
    @Value("${gigachat.web.request.content}")
    private String gigachatWebRequestContent;

    public void askGigachatSlovotbirator(TelegramUser user) {
        try {
            ResponseDto gigaSlovotbiratorResponse = callSlovotbirator(user.getCommunications().get(SLOVOTBIRATOR_REQUEST));
            if (gigaSlovotbiratorResponse == null) {
                telegramUtils.updateTelegramUserMap(gigachatWebErrorsIncorrectRequest, NO_RESPONSE, user);
            } else if (gigachatUtils.isResponseContainGigachatStatus(gigaSlovotbiratorResponse, "FAILURE")) {
                telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidUser, STUPID_USER_RESPONSE, user);
            } else if (gigachatUtils.isResponseContainGigachatStatus(gigaSlovotbiratorResponse, "SUCCESS")) {
                telegramUtils.updateTelegramUserMap(gigaSlovotbiratorResponse.getChoices().get(0).getMessage().getContent(), SLOVOTBIRATOR_RESPONSE, user);
                telegramUtils.updateTelegramUserMap(gigachatUtils.removeGigaStatusFormResponse(gigaSlovotbiratorResponse), DISHES_REQUEST, user);
            } else {
                telegramUtils.updateTelegramUserMap(gigachatWebErrorsIncorrectRequest, NO_RESPONSE, user);
            }
        } catch (IOException e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
            telegramUtils.updateTelegramUserMap(gigachatWebErrorsIncorrectRequest, EXCEPTION_RESPONSE, user);
        }
    }

    public void askGigachatDishes(TelegramUser user) {
        try {
            RequestDto gigaDishesRequest = gigachatUtils.createGigaRequest(user.getCommunications().get(DISHES_REQUEST), gigachatWebFakeMessageBehaviourPath, gigachatWebRequestContent);
            ResponseDto gigaFinalResponse = gigachatClient.askGigachatQuestion(gigaDishesRequest,gigachatWebFakeMessageBehaviourXSessionId);
            if(!gigachatUtils.isValidResponse(gigaFinalResponse) || gigachatUtils.isResponseContainGigachatStatus(gigaFinalResponse, "SUCCESS")) {
                gigaDishesRequest = gigachatUtils.updateGigaRequestForRerequestDishesListGetting(gigaDishesRequest, user.getCommunications().get(DISHES_REQUEST));
                gigaFinalResponse = gigachatClient.askGigachatQuestion(gigaDishesRequest, gigachatWebFakeMessageBehaviourXSessionId);
                if(!gigachatUtils.isValidResponse(gigaFinalResponse) || gigachatUtils.isResponseContainGigachatStatus(gigaFinalResponse, "SUCCESS")) {
                    telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidGiga, STUPID_GIGA_RESPONSE, user);
                }
                telegramUtils.updateTelegramUserMap(gigachatUtils.removeGigaStatusFormResponse(gigaFinalResponse), DISHES_RESPONSE, user);
            } else {
                telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidGiga, STUPID_GIGA_RESPONSE, user);
            }
        } catch (Exception e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
            telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidGiga, EXCEPTION_RESPONSE, user);
        }
    }

    private ResponseDto callSlovotbirator(String userQuestion) throws IOException {
        RequestDto gigaRequestForSlovotbirator = gigachatUtils.createGigaRequest(userQuestion, gigachatWebFakeMessageSlovotbiratorPath, gigachatWebFakeMessageSlovotbiratorRequest);
        ResponseDto gigaSlovotbiratorResponse = gigachatClient.askGigachatQuestion(gigaRequestForSlovotbirator, gigachatWebFakeMessageSlovotbiratorXSessionId);
        if(gigachatUtils.isValidResponse(gigaSlovotbiratorResponse)) {
            return gigaSlovotbiratorResponse;
        } else {
            return null;
        }
    }
}
