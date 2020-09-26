package com.example.hackathon_backend.exceptions;

import com.example.hackathon_backend.models.Message;

public abstract class RegisterException extends Exception {

  public abstract Message getErrorMessage();
}
