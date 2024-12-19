package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.GigachatUtils;
import ru.seraleu.gigachat.web.clients.GigachatClient;
import ru.seraleu.gigachat.web.dto.requests.RequestDto;
import ru.seraleu.gigachat.web.dto.responses.ResponseDto;

import java.io.IOException;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatClientService {

    private final GigachatClient gigachatClient;
    private final GigachatAuthService gigachatAuthService;
    private final GigachatUtils gigachatUtils;
    @Value("${web.gigachat.error-messages-for-user.stupid-giga}")
    private String webGigachatErrorsStupidGiga;
    @Value("${web.gigachat.error-messages-for-user.stupid-user}")
    private String webGigachatErrorsStupidUser;
    @Value("${web.gigachat.error-messages-for-user.incorrect-request}")
    private String webGigachatErrorsIncorrectRequest;

    public String askGigachatQuestion(String question) {
        try {
            gigachatAuthService.updateAuthKey();
            String productsOrError = getProductsFromUserRequest(question);
            if("FAILURE".equalsIgnoreCase(productsOrError)) {
                return webGigachatErrorsIncorrectRequest;
            }
            RequestDto gigaRequestWithBehaviour = gigachatUtils.createGigaRequestForDishesListGetting(productsOrError);
            ResponseDto gigaFinalResponse = gigachatClient.askGigachatQuestion(gigaRequestWithBehaviour);
            if(gigachatUtils.isValidResponse(gigaFinalResponse)) {
                if(gigachatUtils.isResponseContainGigachatStatus(gigaFinalResponse)) {
                    gigaFinalResponse = askGigachatQuestionAgainForValidResponse(gigaRequestWithBehaviour, question);
                    if(gigachatUtils.isValidResponse(gigaFinalResponse)) {
                        if (gigachatUtils.isResponseContainGigachatStatus(gigaFinalResponse)) {
                            return webGigachatErrorsStupidGiga;
                        }
                    }
                }
                return gigachatUtils.prepareGigaResponseForUser(gigaFinalResponse);
            }
        } catch (IOException e) {
            log.error("Exception while gigachat response processing. {}", getStackTrace(e));
        }
        return webGigachatErrorsStupidUser;
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
