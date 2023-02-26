package com.pwawrzyniak.fdademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FdaDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(FdaDemoApplication.class, args);
  }
}