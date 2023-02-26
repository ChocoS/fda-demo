package com.pwawrzyniak.fdademo.infrastructure.openfda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DrugFda {

  private final String applicationNumber;

  private final OpenFda openFda;

  private final List<Product> products;

  @JsonCreator
  public DrugFda(@JsonProperty("application_number") String applicationNumber,
                 @JsonProperty("openfda") OpenFda openFda,
                 @JsonProperty("products") List<Product> products) {
    this.applicationNumber = applicationNumber;
    this.openFda = openFda;
    this.products = Optional.ofNullable(products)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  public Optional<OpenFda> getOpenFda() {
    return Optional.ofNullable(openFda);
  }

  public List<Product> getProducts() {
    return products;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("applicationNumber", applicationNumber)
        .append("openFda", openFda)
        .append("products", products)
        .toString();
  }
}