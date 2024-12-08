package ru.seraleu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GigachatRestClientConfig {

    @Value("${web.gigachat.options.connect-timeout-millis}")
    private Integer webClientOptionsConnectTimeoutMillis;
    @Value("${web.gigachat.options.follow-redirects}")
    private Boolean webClientOptionsFollowRedirects;
    @Value("${web.gigachat.options.read-timeout-millis}")
    private Integer webClientOptionsReadTimeoutMillis;
    @Value("${web.gigachat.retryer.period}")
    private Integer webClientRetryerPeriod;
    @Value("${web.gigachat.retryer.duration}")
    private Integer webClientRetryerDuration;
    @Value("${web.gigachat.retryer.max-attempts}")
    private Integer webClientRetryerMaxAttempts;
    @Value("${web.gigachat.auth.base-url}")
    private String webGigachatAuthBaseUrl;
    @Value("${web.gigachat.client.base-url}")
    private String webGigachatClientBaseUrl;
    @Value("${web.gigachat.auth.key}")
    private String webGigachatAuthKey;

    @Bean("gigachatAuthRestTemplate")
    public RestTemplate gigachatAuthRestTemplate(SslBundles sslBundles) {
        return new RestTemplateBuilder()
                .sslBundle(sslBundles.getBundle("rus"))
                .rootUri(webGigachatAuthBaseUrl)
                .defaultHeader("Authorization", "Basic " + webGigachatAuthKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("RqUID", "7abc13fd-d2bc-43cf-ab98-0ffbaf234afc")
                .build();
    }

    @Bean("gigachatClientRestTemplate")
    public RestTemplate gigachatClientRestTemplate(SslBundles sslBundles) {
        return new RestTemplateBuilder()
                .sslBundle(sslBundles.getBundle("rus"))
                .rootUri(webGigachatClientBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
