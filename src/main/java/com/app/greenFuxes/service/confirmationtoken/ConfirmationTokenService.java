package com.app.greenFuxes.service.confirmationtoken;

import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.confirmationtoken.InvalidConfirmationTokenException;

public interface ConfirmationTokenService {

    ConfirmationToken saveConfirmationToken(ConfirmationToken token);

    void deleteConfirmationToken(Long id);

    ConfirmationToken findByToken(String confirmToken) throws InvalidConfirmationTokenException;

    ConfirmationToken findByUser(User user);
}
