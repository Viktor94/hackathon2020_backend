package com.example.hackathon_backend.exceptions;

import com.example.hackathon_backend.models.Message;

public class WrongPasswordException extends LoginException {

  @Override
  public Message getErrorMessage() {
    return new Message("error", "Wrong password!");
  }
}
