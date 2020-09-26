package com.example.hackathon_backend.exceptionhandler;

import com.example.hackathon_backend.models.httpresponse.HttpResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@NoArgsConstructor
@ControllerAdvice
public class ProgramExceptionHandler {

  private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(
        new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
            message.toUpperCase()), httpStatus);
  }
}
