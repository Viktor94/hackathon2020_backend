package com.app.greenFuxes.service.confirmationtoken;

import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.exception.confirmationtoken.InvalidConfirmationTokenException;
import com.app.greenFuxes.repository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public ConfirmationToken saveConfirmationToken(ConfirmationToken token) {
      return confirmationTokenRepository.save(token);
    }

    @Override
    public void deleteConfirmationToken(Long id) {
        confirmationTokenRepository.deleteById(id);
    }

    @Override
    public ConfirmationToken findByToken(String confirmToken) throws InvalidConfirmationTokenException {
        ConfirmationToken confToken = confirmationTokenRepository.findByConfirmationToken(confirmToken);
        if (confirmToken == null){
            throw new InvalidConfirmationTokenException("The provided token is invalid!");
        }
        if (confToken.getExpirationTime().getTime()- (new Date().getTime()) < 0) {
            throw new InvalidConfirmationTokenException("The provided token is expired!");
        }
        return confToken;
    }
}
