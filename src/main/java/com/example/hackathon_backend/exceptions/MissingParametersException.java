package com.example.hackathon_backend.exceptions;

import com.example.hackathon_backend.models.Message;
import com.example.hackathon_backend.models.dtos.UserLoginDTO;
import java.util.List;

public class MissingParametersException extends LoginException {

  private final UserLoginDTO dto;

  public MissingParametersException(UserLoginDTO dto) {
    this.dto = dto;
  }

  @Override
  public Message getErrorMessage() {
    return new Message("error", buildMessage());
  }

  public String buildMessage() {
    List<String> nullFields = dto.getNullFields();
    String fields = String.join(", ", nullFields);

    return "Missing field(s): " + fields + "!";
  }
}
