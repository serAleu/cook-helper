package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.utils.GigachatUtils;
import ru.seraleu.gigachat.web.clients.GigachatClient;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;
import ru.seraleu.telegram.services.TelegramUtils;
import ru.seraleu.telegram.users.TelegramUser;

import java.io.IOException;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.users.TelegramCommunicationStep.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClientService {

    private final GigachatClient gigachatClient;
    private final GigachatAuthService gigachatAuthService;
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

    public void askGigachatSlovotbirator(TelegramUser user) {
        try {
            ResponseDto gigaSlovotbiratorResponse = callSlovotbirator(user.getUserCommunications().get(SLOVOTBIRATOR_REQUEST));
            if (gigaSlovotbiratorResponse == null) {
                telegramUtils.updateTelegramUserMap(gigachatWebErrorsIncorrectRequest, NO_RESPONSE, user);
            } else if (gigachatUtils.isResponseContainGigachatStatus(gigaSlovotbiratorResponse, "FAILURE")) {
                telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidUser, STUPID_USER_RESPONSE, user);
            } else if (gigachatUtils.isResponseContainGigachatStatus(gigaSlovotbiratorResponse, "SUCCESS")) {
                telegramUtils.updateTelegramUserMap(gigaSlovotbiratorResponse.getChoices().get(0).getMessage().getContent(), SLOVOTBIRATOR_RESPONSE, user);
                telegramUtils.updateTelegramUserMap(gigachatUtils.removeGigaStatusFormResponse(gigaSlovotbiratorResponse), DISHES_REQUEST, user);
            }
        } catch (IOException e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
            telegramUtils.updateTelegramUserMap(gigachatWebErrorsIncorrectRequest, EXCEPTION_RESPONSE, user);
        }
    }

    public void askGigachatDishes(TelegramUser user) {
        try {
            gigachatAuthService.updateAuthKey();
            RequestDto gigaDishesRequest = gigachatUtils.createGigaRequestForDishes(user.getUserCommunications().get(DISHES_REQUEST));
            ResponseDto gigaFinalResponse = gigachatClient.askGigachatQuestion(gigaDishesRequest,gigachatWebFakeMessageBehaviourXSessionId);
            if(!gigachatUtils.isValidResponse(gigaFinalResponse) || gigachatUtils.isResponseNotContainGigachatStatus(gigaFinalResponse)) {
                gigaFinalResponse = askGigachatQuestionAgainForValidResponse(gigaDishesRequest, user.getUserCommunications().get(DISHES_REQUEST));
                if(!gigachatUtils.isValidResponse(gigaFinalResponse) || gigachatUtils.isResponseNotContainGigachatStatus(gigaFinalResponse)) {
                    telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidGiga, STUPID_GIGA_RESPONSE, user);
                }
                telegramUtils.updateTelegramUserMap(gigachatUtils.removeGigaStatusFormResponse(gigaFinalResponse), DISHES_RESPONSE, user);
            } else {
                telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidGiga, STUPID_GIGA_RESPONSE, user);
            }
        } catch (IOException e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
            telegramUtils.updateTelegramUserMap(gigachatWebErrorsStupidGiga, EXCEPTION_RESPONSE, user);
        }
    }

    private ResponseDto callSlovotbirator(String question) throws IOException {
        gigachatAuthService.updateAuthKey();
        RequestDto gigaRequestForSlovotbirator = gigachatUtils.createGigaRequestForSlovotbiratorCalling(question);
        ResponseDto gigaSlovotbiratorResponse = gigachatClient.askGigachatQuestion(gigaRequestForSlovotbirator, gigachatWebFakeMessageSlovotbiratorXSessionId);
        if(gigachatUtils.isValidResponse(gigaSlovotbiratorResponse)) {
            return gigaSlovotbiratorResponse;
        } else {
            return null;
        }
    }

    private ResponseDto askGigachatQuestionAgainForValidResponse(RequestDto requestDto, String question) throws IOException {
        gigachatAuthService.updateAuthKey();
        requestDto = gigachatUtils.updateGigaRequestForRerequestDishesListGetting(requestDto, question);
        return gigachatClient.askGigachatQuestion(requestDto, gigachatWebFakeMessageBehaviourXSessionId);
    }
}
