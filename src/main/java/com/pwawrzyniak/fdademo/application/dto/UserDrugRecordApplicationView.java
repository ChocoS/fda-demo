package com.pwawrzyniak.fdademo.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserDrugRecordApplicationView {

  private final String applicationNumber;

  private final String manufacturerName;

  private final String substanceName;

  private final List<String> productNames;

  @JsonCreator
  public UserDrugRecordApplicationView(@JsonProperty("applicationNumber") String applicationNumber,
                                       @JsonProperty("manufacturerName") String manufacturerName,
                                       @JsonProperty("substanceName") String substanceName,
                                       @JsonProperty("productNames") List<String> productNames) {
    this.applicationNumber = applicationNumber;
    this.manufacturerName = manufacturerName;
    this.substanceName = substanceName;
    this.productNames = Optional.ofNullable(productNames).map(Collections::unmodifiableList).orElse(Collections.emptyList());
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

  public static UserDrugRecordApplicationView fromUserDrugRecordApplicationDto(UserDrugRecordApplication userDrugRecordApplication) {
    return new UserDrugRecordApplicationView(userDrugRecordApplication.getApplicationNumber(),
        userDrugRecordApplication.getManufacturerName(),
        userDrugRecordApplication.getSubstanceName(),
        userDrugRecordApplication.getProductNames());
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