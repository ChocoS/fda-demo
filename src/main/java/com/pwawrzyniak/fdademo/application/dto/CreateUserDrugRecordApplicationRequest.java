package com.pwawrzyniak.fdademo.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class CreateUserDrugRecordApplicationRequest {

  @NotBlank
  private final String applicationNumber;

  private final String manufacturerName;

  private final String substanceName;

  private final List<String> productNames;

  @JsonCreator
  public CreateUserDrugRecordApplicationRequest(@JsonProperty("applicationNumber") String applicationNumber,
                                                @JsonProperty("manufacturerName") String manufacturerName,
                                                @JsonProperty("substanceName") String substanceName,
                                                @JsonProperty("productNames") List<String> productNames) {
    this.applicationNumber = applicationNumber;
    this.manufacturerName = manufacturerName;
    this.substanceName = substanceName;
    this.productNames = productNames;
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public String getSubstanceName() {
    return substanceName;
  }

  public List<String> getProductNames() {
    return productNames;
  }

  public UserDrugRecordApplication toUserDrugRecordApplication() {
    return UserDrugRecordApplication.builder()
        .withApplicationNumber(applicationNumber)
        .withManufacturerName(manufacturerName)
        .withSubstanceName(substanceName)
        .withProductNames(productNames)
        .build();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("applicationNumber", applicationNumber)
        .append("manufacturerName", manufacturerName)
        .append("substanceName", substanceName)
        .append("productNames", productNames)
        .toString();
  }
}