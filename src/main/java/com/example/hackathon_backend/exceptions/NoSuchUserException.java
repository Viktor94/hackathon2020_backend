package com.example.hackathon_backend.exceptions;

import com.example.hackathon_backend.models.Message;
import com.example.hackathon_backend.models.dtos.UserLoginDTO;

public class NoSuchUserException extends LoginException {

  private final String username;

  public NoSuchUserException(UserLoginDTO dto) {
    this.username = dto.getUsername();
  }

  @Override
  public Message getErrorMessage() {
    return new Message("error", "No such user: " + this.username + "!");
  }
}
