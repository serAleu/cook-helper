package ru.seraleu.gigachat.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.seraleu.gigachat.utils.GigachatStaticContext;
import ru.seraleu.gigachat.web.clients.GigachatAuthClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class GigachatAuthService {

    private final GigachatAuthClient gigachatAuthClient;
    @Value("${web.gigachat.auth.last-auth-key-path}")
    private String webGigachatAuthLastAuthKeyPath;

    public void updateAuthKey() throws IOException {
        if(StringUtils.isBlank(GigachatStaticContext.authKey)) {
            GigachatStaticContext.authKey = gigachatAuthClient.getGigachatAuthKey();
            if(!StringUtils.isBlank(GigachatStaticContext.authKey)){
                keepLastAuthKey();
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void keepLastAuthKey() throws IOException {
        File file = new File(webGigachatAuthLastAuthKeyPath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(GigachatStaticContext.authKey);
        } catch (IOException e) {
            log.error("Error while Gigachat auth key saving. stackTrace: {}", Arrays.toString(e.getStackTrace()));
        }
    }

}
