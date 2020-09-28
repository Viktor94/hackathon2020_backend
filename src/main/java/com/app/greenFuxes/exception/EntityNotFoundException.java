package com.app.greenFuxes.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(Class entityClass) {
        super(String.format("No %s found with the specified ID!", entityClass.getSimpleName()));
    }
}
