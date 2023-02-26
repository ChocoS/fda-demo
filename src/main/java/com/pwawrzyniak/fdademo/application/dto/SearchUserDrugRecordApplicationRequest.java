package com.pwawrzyniak.fdademo.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SearchUserDrugRecordApplicationRequest {

  private final String manufacturerName;

  private final String substanceName;

  @JsonCreator
  public SearchUserDrugRecordApplicationRequest(@JsonProperty("manufacturerName") String manufacturerName,
                                                @JsonProperty("substanceName") String substanceName) {
    this.manufacturerName = manufacturerName;
    this.substanceName = substanceName;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public String getSubstanceName() {
    return substanceName;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("manufacturerName", manufacturerName)
        .append("substanceName", substanceName)
        .toString();
  }
}