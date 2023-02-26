package com.pwawrzyniak.fdademo.infrastructure.openfda.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DrugFdaResponse {

  private final List<DrugFda> results;

  private final Meta meta;

  @JsonCreator
  public DrugFdaResponse(@JsonProperty("results") List<DrugFda> results,
                         @JsonProperty("meta") Meta meta) {
    this.results = Optional.ofNullable(results)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
    this.meta = meta;
  }

  public List<DrugFda> getResults() {
    return results;
  }

  public Optional<Meta> getMeta() {
    return Optional.ofNullable(meta);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("results", results)
        .append("meta", meta)
        .toString();
  }
}