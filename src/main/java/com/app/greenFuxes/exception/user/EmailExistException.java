package com.app.greenFuxes.exception.user;

public class EmailExistException extends UserManipulationException {
    public EmailExistException(String message) {
        super(message);
    }
}
