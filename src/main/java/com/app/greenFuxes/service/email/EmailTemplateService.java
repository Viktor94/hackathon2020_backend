package com.app.greenFuxes.service.email;

import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.security.SecurityConstant;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

public class EmailTemplateService {
  private static String CONFIRMATION_MIDDLE_URL = "/users/confirm?token=";

  public static String VERIFICATION_EMAIL(User user, ConfirmationToken confirmationToken) {
    String htmlString = readHtml("verificationEmailTemplate.html");
    String htmlReplacedData =
        htmlString
            .replace("[USERNAME]", user.getUserName())
            .replace(
                "[URL_AND_TOKEN]",
                SecurityConstant.APP_BASE_URL
                    + CONFIRMATION_MIDDLE_URL
                    + confirmationToken.getConfirmationToken());
    return htmlReplacedData;
  }

  public static String QUEUE_NOTIFICATION_EMAIL(User user, Integer lunchtimeInMinute) {
    String htmlString = readHtml("queueNotificationTemplate.html");
    String htmlReplacedData =
        htmlString
            .replace("[USERNAME]", user.getUserName())
            .replace("[LUNCH_TIME]", String.valueOf(lunchtimeInMinute));
    return htmlReplacedData;
  }

  public static String KICK_NOTIFICATION_EMAIL(User user) {
    String htmlString = readHtml("kickNotificationTemplate.html");
    String htmlReplacedData = htmlString.replace("[USERNAME]", user.getUserName());
    return htmlReplacedData;
  }

  public static String LESS_THEN_FIVE_MIN_NOTIFICATION_EMAIL(
      User user, Integer lunchtimeLeftInMinute) {
    String htmlString = readHtml("lessThenfiveMinNotificationTemplate.html");
    String htmlReplacedData =
        htmlString
            .replace("[USERNAME]", user.getUserName())
            .replace("[LEFT_TIME]", String.valueOf(lunchtimeLeftInMinute));
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
