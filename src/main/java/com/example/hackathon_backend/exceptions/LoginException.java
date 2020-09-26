package com.example.hackathon_backend.exceptions;


import com.example.hackathon_backend.models.Message;

public abstract class LoginException extends Exception {

  public abstract Message getErrorMessage();
}
