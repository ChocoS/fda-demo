package com.pwawrzyniak.fdademo.infrastructure.openfda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Optional;

public class MetaResults {

  private final Long total;

  @JsonCreator
  public MetaResults(@JsonProperty("total") Long total) {
    this.total = total;
  }

  public Optional<Long> getTotal() {
    return Optional.ofNullable(total);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("total", total)
        .toString();
  }
}