package com.pwawrzyniak.fdademo.infrastructure.openfda;

import org.springframework.http.HttpStatus;

public class OpenFdaFeignClientException extends Exception {

  private final HttpStatus httpStatus;

  public OpenFdaFeignClientException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}