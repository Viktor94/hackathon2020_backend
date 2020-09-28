package com.app.greenFuxes.exception.user;

public class UsernameExistException extends UserManipulationException {
    public UsernameExistException(String message) {
        super(message);
    }
}
