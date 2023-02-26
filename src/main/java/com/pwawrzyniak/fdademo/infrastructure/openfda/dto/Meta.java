package com.pwawrzyniak.fdademo.infrastructure.openfda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Optional;

public class Meta {

  private final MetaResults results;

  @JsonCreator
  public Meta(@JsonProperty("results") MetaResults results) {
    this.results = results;
  }

  public Optional<MetaResults> getResults() {
    return Optional.ofNullable(results);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("results", results)
        .toString();
  }
}