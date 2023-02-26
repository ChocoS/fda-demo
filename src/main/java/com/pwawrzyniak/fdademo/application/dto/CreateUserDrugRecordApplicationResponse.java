package com.pwawrzyniak.fdademo.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CreateUserDrugRecordApplicationResponse {

  private final String applicationNumber;

  @JsonCreator
  public CreateUserDrugRecordApplicationResponse(@JsonProperty("applicationNumber") String applicationNumber) {
    this.applicationNumber = applicationNumber;
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("applicationNumber", applicationNumber)
        .toString();
  }
}