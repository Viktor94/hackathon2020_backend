package com.app.greenFuxes.service.email;

import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.entity.user.User;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class EmailSenderServiceImpl implements EmailSenderService {

  private String senderEmailUsername = System.getenv("EMAIL_USERNAME"); // change accordingly
  private String senderEmailPassword = System.getenv("EMAIL_PASSWORD"); // change accordingly

  private Logger LOGGER;

  @Autowired
  public EmailSenderServiceImpl(Logger LOGGER) {
    this.LOGGER = LOGGER;
  }

  @Override
  public void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken) {
    String to = user.getEmail();
    String subject = "Complete Registration!";
    sendHTMLEmail(
        generateHTMLMessage(
            to, subject, EmailTemplateService.VERIFICATION_EMAIL(user, confirmationToken)));
    System.out.println("Sent message successfully....");
    LOGGER.info("Sent VerificationEmailHTML successfully");
  }

  @Override
  public void sendQueueNotificationEmail(User user, Integer lunchtimeInMinute) {
    String to = user.getEmail();
    String subject = "Time to lunch!";
    sendHTMLEmail(
        generateHTMLMessage(
            to, subject, EmailTemplateService.QUEUE_NOTIFICATION_EMAIL(user, lunchtimeInMinute)));
  }

  @Override
  public void sendKickFromCanteenNotificationEmail(User user) {
    String to = user.getEmail();
    String subject = "Time to leave the canteen!";
    sendHTMLEmail(
        generateHTMLMessage(to, subject, EmailTemplateService.KICK_NOTIFICATION_EMAIL(user)));
  }

  @Override
  public void sendLessThenFiveMinLeftNotificationEmail(User user, Integer lunchtimeLeftInMinute) {
    String to = user.getEmail();
    String subject = "You have left " + lunchtimeLeftInMinute + " minute to lunch!";
    sendHTMLEmail(
        generateHTMLMessage(
            to,
            subject,
            EmailTemplateService.LESS_THEN_FIVE_MIN_NOTIFICATION_EMAIL(
                user, lunchtimeLeftInMinute)));
  }

  private Message generateHTMLMessage(String to, String subject, String template) {
    // Sender's email ID needs to be mentioned
    String from = System.getenv("EMAIL_USERNAME");

    try {
      // Create a default MimeMessage object.
      Message message = new MimeMessage(generateSession(generateGmailProperties()));

      // Set From: header field of the header.
      message.setFrom(new InternetAddress(from));

      // Set To: header field of the header.
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

      // Set Subject: header field
      message.setSubject(subject);

      // Send the actual HTML message, as big as you like
      message.setContent(template, "text/html");

      return message;
    } catch (MessagingException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Async
  private void sendHTMLEmail(Message message) {
    try {
      Transport.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
      LOGGER.warn("Messaging Exception: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private Session generateSession(Properties props) {
    // Get the Session object.
    Session session =
        Session.getInstance(
            props,
            new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmailUsername, senderEmailPassword);
              }
            });
    return session;
  }

  private Properties generateGmailProperties() {
    // Assuming you are sending email through smtp.gmail.com
    String host = "smtp.gmail.com";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", "587");
    return props;
  }
}
