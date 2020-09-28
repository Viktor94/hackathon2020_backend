package com.app.greenFuxes.service.email;

import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.entity.user.User;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

@Service("emailSenderService")
public class EmailSenderServiceImpl implements EmailSenderService {

    private String senderEmailUsername = System.getenv("EMAIL_USERNAME");//change accordingly
    private String senderEmailPassword = System.getenv("EMAIL_PASSWORD");//change accordingly

    private Logger LOGGER;

    @Autowired
    public EmailSenderServiceImpl(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    @Override
    public void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken) throws IOException {
        String to = user.getEmail();
        String subject = "Complete Registration!";

        // Send message
        sendHTMLEmail(generateHTMLMessage(to, subject, EmailTemplateService.VERIFICATION_EMAIL(user, confirmationToken)));
        System.out.println("Sent message successfully....");
        LOGGER.info("Sent VerificationEmailHTML successfully");
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
        Session session = Session.getInstance(props,
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

