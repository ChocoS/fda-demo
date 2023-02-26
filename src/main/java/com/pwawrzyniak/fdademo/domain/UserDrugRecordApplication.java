package com.pwawrzyniak.fdademo.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserDrugRecordApplication {

  private final String applicationNumber;

  private final String manufacturerName;

  private final String substanceName;

  private final List<String> productNames;

  private UserDrugRecordApplication(Builder builder) {
    applicationNumber = builder.applicationNumber;
    manufacturerName = builder.manufacturerName;
    substanceName = builder.substanceName;
    productNames = Optional.ofNullable(builder.productNames)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
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

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String applicationNumber;

    private String manufacturerName;

    private String substanceName;

    private List<String> productNames;

    private Builder() {
    }

    public Builder withApplicationNumber(String applicationNumber) {
      this.applicationNumber = applicationNumber;
      return this;
    }

    public Builder withManufacturerName(String manufacturerName) {
      this.manufacturerName = manufacturerName;
      return this;
    }

    public Builder withSubstanceName(String substanceName) {
      this.substanceName = substanceName;
      return this;
    }

    public Builder withProductNames(List<String> productNames) {
      this.productNames = productNames;
      return this;
    }

    public UserDrugRecordApplication build() {
      return new UserDrugRecordApplication(this);
    }
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