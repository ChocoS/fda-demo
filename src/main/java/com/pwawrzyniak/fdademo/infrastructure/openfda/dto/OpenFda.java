package com.pwawrzyniak.fdademo.infrastructure.openfda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OpenFda {

  private final List<String> manufacturerNames;

  private final List<String> substanceNames;

  private final List<String> brandNames;

  @JsonCreator
  public OpenFda(@JsonProperty("manufacturer_name") List<String> manufacturerNames,
                 @JsonProperty("substance_name") List<String> substanceNames,
                 @JsonProperty("brand_name") List<String> brandNames) {
    this.manufacturerNames = Optional.ofNullable(manufacturerNames)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
    this.substanceNames = Optional.ofNullable(substanceNames)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
    this.brandNames = Optional.ofNullable(brandNames)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }

  public List<String> getManufacturerNames() {
    return manufacturerNames;
  }

  public List<String> getSubstanceNames() {
    return substanceNames;
  }

  public List<String> getBrandNames() {
    return brandNames;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("manufacturerNames", manufacturerNames)
        .append("substanceNames", substanceNames)
        .append("brandNames", brandNames)
        .toString();
  }
}