package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.utils.GigachatAuthContext;
import ru.seraleu.gigachat.web.clients.GigachatAuthClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatAuthService {

    private final GigachatAuthClient gigachatAuthClient;
    @Value("${web.gigachat.auth.last-auth-key-path}")
    private String webGigachatAuthLastAuthKeyPath;

    public void updateAuthKey() throws IOException {
        if(StringUtils.isBlank(GigachatAuthContext.accessToken) || isAccessTokenExpired()) {
            String responseJson = null;
            synchronized (GigachatAuthContext.class) {
                responseJson = gigachatAuthClient.getGigachatAuthKey();
            }
            if(!StringUtils.isBlank(GigachatAuthContext.accessToken)) {
                keepLastAuthKey(responseJson);
            }
        }
    }

    private boolean isAccessTokenExpired() {
        if (GigachatAuthContext.expiresAt != null) {
            return LocalDateTime.now().isAfter(LocalDateTime.ofInstant(Instant.ofEpochMilli(GigachatAuthContext.expiresAt), TimeZone.getDefault().toZoneId()));
        }
        return true;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void keepLastAuthKey(String responseJson) throws IOException {
        File file = new File(webGigachatAuthLastAuthKeyPath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(responseJson);
        } catch (IOException e) {
            log.error("Error while Gigachat auth key saving. stackTrace: {}", Arrays.toString(e.getStackTrace()));
        }
    }

}
