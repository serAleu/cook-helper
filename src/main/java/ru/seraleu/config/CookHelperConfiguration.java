package ru.seraleu.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.seraleu.gigachat.utils.GigachatAuthContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

@Configuration
@EnableScheduling
@Slf4j
public class CookHelperConfiguration {

    @Value("${web.gigachat.auth.last-auth-key-path}")
    private String webGigachatAuthLastAuthKeyPath;

    @Bean("gigachatAuthContext")
    public GigachatAuthContext gigachatAuthContext() throws IOException {
        GigachatAuthContext gigachatAuthContext = new GigachatAuthContext();
        File file = new File(webGigachatAuthLastAuthKeyPath);
        if(new File(webGigachatAuthLastAuthKeyPath).isFile()) {
            String lastAuthResponseJson = Files.readString(file.toPath(), Charset.defaultCharset());
            if(!StringUtils.isBlank(lastAuthResponseJson)) {
                try {
                    gigachatAuthContext = objectMapper().readValue(lastAuthResponseJson, GigachatAuthContext.class);
                } catch (Exception e) {
                    log.error("Exception while getting Gigachat last auth context from the file. last_response_json = {}, exception = {}", lastAuthResponseJson, e.getStackTrace());
                }
            }
        }
        return gigachatAuthContext;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
