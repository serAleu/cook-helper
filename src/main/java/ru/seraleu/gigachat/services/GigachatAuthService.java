package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.utils.GigachatAuthContext;
import ru.seraleu.gigachat.web.clients.GigachatAuthClient;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static ru.seraleu.gigachat.utils.GigachatAuthContext.setAccessTokenAndExpiresAt;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatAuthService {

    private final GigachatAuthClient gigachatAuthClient;
    @Value("${gigachat.web.auth.last-auth-key-path}")
    private String gigachatWebAuthLastAuthKeyPath;

    public synchronized void updateAuthKey() throws IOException {
        if (StringUtils.isBlank(GigachatAuthContext.accessToken) || isAccessTokenExpired()) {
            getAccessTokenFromFile();
            if (isAccessTokenExpired()) {
                String responseJson = gigachatAuthClient.getGigachatAuthKey();
                if (!StringUtils.isBlank(GigachatAuthContext.accessToken)) {
                    keepLastAuthKey(responseJson);
                }
            }
        }
    }

    private boolean isAccessTokenExpired() {
        if (GigachatAuthContext.expiresAt != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expirationTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(GigachatAuthContext.expiresAt), TimeZone.getDefault().toZoneId());
            boolean nowIsAfterExpirationTime = now.isAfter(expirationTime);
            log.info("Giga token time expire checking. now = {}, expirationTime = {}, nowIsAfterExpirationTime = {}", now, expirationTime, nowIsAfterExpirationTime);
            return nowIsAfterExpirationTime;
        }
        return true;
    }

    private void getAccessTokenFromFile() {
        try {
            File file = new File(gigachatWebAuthLastAuthKeyPath);
            if(file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    setAccessTokenAndExpiresAt(reader.readLine());
                }
            }
        } catch (Exception e) {
            log.error("Exception while getting last giga auth json from file. {}", getStackTrace(e));
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void keepLastAuthKey(String responseJson) throws IOException {
        File file = new File(gigachatWebAuthLastAuthKeyPath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(responseJson);
        } catch (IOException e) {
            log.error("Error while Gigachat auth key saving. stackTrace: {}", getStackTrace(e));
        }
    }

}
