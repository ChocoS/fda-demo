package com.pwawrzyniak.fdademo.infrastructure.openfda;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

class OpenFdaFeignClientConfiguration {

  @Bean
  ErrorDecoder openFdaFeignClientErrorDecoder() {
    return new OpenFdaFeignClientErrorDecoder();
  }

  static class OpenFdaFeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
      return new OpenFdaFeignClientException(String.format("Request to Open FDA failed with status %d", response.status()), HttpStatus.resolve(response.status()));
    }
  }
}