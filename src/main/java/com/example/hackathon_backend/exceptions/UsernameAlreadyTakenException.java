package com.example.hackathon_backend.exceptions;

import com.example.hackathon_backend.models.Message;

public class UsernameAlreadyTakenException extends LoginException {

  private final String username;

  public UsernameAlreadyTakenException(String username) {
    this.username = username;
  }

  @Override
  public Message getErrorMessage() {
    return new Message("error", username + " is already taken, please choose another one!");
  }
}
