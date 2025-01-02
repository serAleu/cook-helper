package ru.seraleu.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.seraleu.gigachat.utils.GigachatAuthContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Configuration
@Slf4j
public class GigachatRestClientConfig {

    @Value("${gigachat.web.options.connect-timeout-millis}")
    private Integer gigachatWebClientOptionsConnectTimeoutMillis;
    @Value("${gigachat.web.options.follow-redirects}")
    private Boolean gigachatWebClientOptionsFollowRedirects;
    @Value("${gigachat.web.options.read-timeout-millis}")
    private Integer gigachatWebClientOptionsReadTimeoutMillis;
    @Value("${gigachat.web.retryer.period}")
    private Integer gigachatWebClientRetryerPeriod;
    @Value("${gigachat.web.retryer.duration}")
    private Integer gigachatWebClientRetryerDuration;
    @Value("${gigachat.web.retryer.max-attempts}")
    private Integer gigachatWebClientRetryerMaxAttempts;
    @Value("${gigachat.web.auth.base-url}")
    private String gigachatWebAuthBaseUrl;
    @Value("${gigachat.web.client.base-url}")
    private String gigachatWebClientBaseUrl;
    @Value("${gigachat.web.auth.key}")
    private String gigachatWebAuthKey;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean("gigachatAuthRestTemplate")
    public RestTemplate gigachatAuthRestTemplate(SslBundles sslBundles) {
        return new RestTemplateBuilder()
                .sslBundle(sslBundles.getBundle("rus"))
                .rootUri(gigachatWebAuthBaseUrl)
                .defaultHeader("Authorization", "Basic " + gigachatWebAuthKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("RqUID", "7abc13fd-d2bc-43cf-ab98-0ffbaf234afc")
                .build();
    }

    @Bean("gigachatClientRestTemplate")
    public RestTemplate gigachatClientRestTemplate(SslBundles sslBundles) {
        return new RestTemplateBuilder()
                .sslBundle(sslBundles.getBundle("rus"))
                .rootUri(gigachatWebClientBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
