package com.app.greenFuxes.service.email;

import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.security.SecurityConstant;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

public class EmailTemplateService {
    private static String CONFIRMATION_MIDDLE_URL = "/users/confirm?token=";

    public static String VERIFICATION_EMAIL(User user, ConfirmationToken confirmationToken) {
        String htmlString = readHtml("verificationEmailTemplate.html");
        String htmlReplacedData = htmlString.replace("[USERNAME]", user.getUserName()).replace("[URL_AND_TOKEN]", SecurityConstant.APP_BASE_URL + CONFIRMATION_MIDDLE_URL + confirmationToken.getConfirmationToken());
        return htmlReplacedData;
    }

    public static String QUEUE_NOTIFICATION_EMAIL(User user, Integer lunchtimeInMinute) {
        String htmlString = readHtml("queueNotificationTemplate.html");
        String htmlReplacedData = htmlString.replace("[USERNAME]", user.getUserName()).replace("[LUNCH_TIME]", String.valueOf(lunchtimeInMinute));
        return htmlReplacedData;
    }

    private static String readHtml(String filename) {
        try {
            InputStream is = new ClassPathResource(filename).getInputStream();
            return IOUtils.toString(is);
        } catch (IOException e) {
            return null;
        }
    }
}
