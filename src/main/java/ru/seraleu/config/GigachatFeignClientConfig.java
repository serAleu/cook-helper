package ru.seraleu.config;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.seraleu.web.gigachat.client.GigachatAuthFeignClient;

import java.util.concurrent.TimeUnit;

import static feign.Logger.Level.FULL;
import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class GigachatFeignClientConfig {

    @Value("${web.feign.gigachat.options.connect-timeout-millis}")
    private Integer webClientFeignOptionsConnectTimeoutMillis;
    @Value("${web.feign.gigachat.options.follow-redirects}")
    private Boolean webClientFeignOptionsFollowRedirects;
    @Value("${web.feign.gigachat.options.read-timeout-millis}")
    private Integer webClientFeignOptionsReadTimeoutMillis;
    @Value("${web.feign.gigachat.retryer.period}")
    private Integer webClientFeignRetryerPeriod;
    @Value("${web.feign.gigachat.retryer.duration}")
    private Integer webClientFeignRetryerDuration;
    @Value("${web.feign.gigachat.retryer.max-attempts}")
    private Integer webClientFeignRetryerMaxAttempts;
    @Value("${web.feign.gigachat.auth.url}")
    private String webFeignGigachatAuthUrl;
    @Value("${web.feign.gigachat.auth.key}")
    private String webFeignGigachatAuthKey;
    @Bean
    public GigachatAuthFeignClient gigachatAuthFeignClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
//                .decoder(new JacksonDecoder())
                .options(new Request.Options((long) webClientFeignOptionsReadTimeoutMillis,
                        TimeUnit.MILLISECONDS, (long) webClientFeignOptionsConnectTimeoutMillis, TimeUnit.MILLISECONDS,
                        webClientFeignOptionsFollowRedirects))
                .retryer(new Retryer.Default(webClientFeignRetryerPeriod,
                        SECONDS.toMillis(webClientFeignRetryerDuration), webClientFeignRetryerMaxAttempts))
                .logLevel(FULL)
                .requestInterceptor(new BasicAuthRequestInterceptor("a5f617d8-35e0-49c8-b284-8c2e20a119f6", webFeignGigachatAuthKey))
                .target(GigachatAuthFeignClient.class, webFeignGigachatAuthUrl);
    }
}
