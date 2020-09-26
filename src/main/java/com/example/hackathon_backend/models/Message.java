package com.example.hackathon_backend.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Message {

  private String status;
  private String message;

  public Message(String status, String message) {
    this.status = status;
    this.message = message;
  }
}
