package com.pwawrzyniak.fdademo.infrastructure.mongo;

public class UserDrugRecordApplicationStorageServiceDuplicateKeyException extends RuntimeException {

  public UserDrugRecordApplicationStorageServiceDuplicateKeyException(String message) {
    super(message);
  }
}