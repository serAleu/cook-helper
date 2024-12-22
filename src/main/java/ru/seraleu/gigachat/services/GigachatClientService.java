package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.GigachatUtils;
import ru.seraleu.gigachat.web.clients.GigachatClient;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;
import ru.seraleu.telegram.users.TelegramUser;

import java.io.IOException;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.telegram.users.TelegramRequestType.*;
import static ru.seraleu.telegram.users.TelegramResponseType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClientService {

    private final GigachatClient gigachatClient;
    private final GigachatAuthService gigachatAuthService;
    private final GigachatUtils gigachatUtils;
    @Value("${gigachat.web.error-messages-for-user.stupid-giga}")
    private String gigachatWebErrorsStupidGiga;
    @Value("${gigachat.web.error-messages-for-user.stupid-user}")
    private String gigachatWebErrorsStupidUser;
    @Value("${gigachat.web.error-messages-for-user.incorrect-request}")
    private String gigachatWebErrorsIncorrectRequest;

    public TelegramUser askGigachatForGettingProductList(TelegramUser user) {
        try {
            gigachatAuthService.updateAuthKey();
            String gigaListOfProductsResponse = getProductsFromUserRequest(user.getRequestsMap().get(LIST_OF_PRODUCT_REQUEST));
            if (StringUtils.isBlank(gigaListOfProductsResponse) || StringUtils.containsIgnoreCase("FAILURE", gigaListOfProductsResponse)) {
                user.getResponsesMap().put(SWEARING_RESPONSE, gigachatWebErrorsIncorrectRequest);
            }
            user.getResponsesMap().put(LIST_OF_PRODUCTS_RESPONSE, gigaListOfProductsResponse);
            user.getRequestsMap().put(LIST_OF_DISHES_REQUEST, gigaListOfProductsResponse);
        } catch (IOException e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
            user.getResponsesMap().put(EXCEPTION_RESPONSE, gigachatWebErrorsIncorrectRequest);
        }
        return user;
    }

    public TelegramUser askGigachatQuestion(TelegramUser user) {
        try {
            gigachatAuthService.updateAuthKey();
            RequestDto gigaRequestWithBehaviour = gigachatUtils.createGigaRequestForDishesListGetting(user.getRequestsMap().get(LIST_OF_DISHES_REQUEST));
            ResponseDto gigaFinalResponse = gigachatClient.askGigachatQuestion(gigaRequestWithBehaviour);
            if(!gigachatUtils.isValidResponse(gigaFinalResponse) || gigachatUtils.isResponseNotContainGigachatStatus(gigaFinalResponse)) {
                gigaFinalResponse = askGigachatQuestionAgainForValidResponse(gigaRequestWithBehaviour, user.getRequestsMap().get(LIST_OF_DISHES_REQUEST));
                if(!gigachatUtils.isValidResponse(gigaFinalResponse) || gigachatUtils.isResponseNotContainGigachatStatus(gigaFinalResponse)) {
                    user.getResponsesMap().put(STUPID_GIGA_RESPONSE, gigachatWebErrorsStupidGiga);
                    return user;
                }
            }
            user.getResponsesMap().put(LIST_OF_DISHES_RESPONSE, gigachatUtils.prepareGigaResponseForUser(gigaFinalResponse));
            return user;
        } catch (IOException e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
            user.getResponsesMap().put(EXCEPTION_RESPONSE, gigachatWebErrorsStupidGiga);
            return user;
        }
    }

    private String getProductsFromUserRequest(String question) {
        RequestDto gigaRequestForGettingProductList = gigachatUtils.createGigaRequestForGettingListOfProduct(question);
        ResponseDto gigaProductsResponse = gigachatClient.askGigachatQuestion(gigaRequestForGettingProductList);
        if(gigachatUtils.isValidResponse(gigaProductsResponse)) {
            return gigachatUtils.parseGigaProductListResponseToString(gigaProductsResponse);
        } else {
            return "FAILURE";
        }
    }

    private ResponseDto askGigachatQuestionAgainForValidResponse(RequestDto requestDto, String question) throws IOException {
        gigachatAuthService.updateAuthKey();
        requestDto = gigachatUtils.updateGigaRequestForRerequestDishesListGetting(requestDto, question);
        return gigachatClient.askGigachatQuestion(requestDto);
    }
}
