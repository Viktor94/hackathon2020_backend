package com.app.greenFuxes.service.email;

import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.entity.user.User;

public interface EmailSenderService {

  void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken);

  void sendQueueNotificationEmail(User user, Integer lunchtimeInMinute);

  void sendKickFromCanteenNotificationEmail(User user);

  void sendLessThenFiveMinLeftNotificationEmail(User user, Integer lunchtimeLeftInMinute);
}
