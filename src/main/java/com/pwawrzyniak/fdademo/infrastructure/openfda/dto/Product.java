package com.pwawrzyniak.fdademo.infrastructure.openfda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Product {

  private final String productNumber;

  @JsonCreator
  public Product(@JsonProperty("product_number") String productNumber) {
    this.productNumber = productNumber;
  }

  public String getProductNumber() {
    return productNumber;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("productNumber", productNumber)
        .toString();
  }
}