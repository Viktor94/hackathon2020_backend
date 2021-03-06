package com.app.greenFuxes.dto.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@NoArgsConstructor
public class HttpResponse {
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "MM-dd-yyyy hh:mm:ss",
      timezone = "Europe/Budapest")
  private Date timeStamp;

  private int httpStatusCode;
  private HttpStatus httpStatus;
  private String reason;
  private String message;

  public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
    this.timeStamp = new Date();
    this.httpStatusCode = httpStatusCode;
    this.httpStatus = httpStatus;
    this.reason = reason;
    this.message = message;
  }
}
