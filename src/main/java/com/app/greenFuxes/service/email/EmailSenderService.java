package com.app.greenFuxes.service.email;

import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.entity.user.ConfirmationToken;

import java.io.IOException;

public interface EmailSenderService {

    void sendVerificationEmailHTML(User user, ConfirmationToken confirmationToken) throws IOException;
}
