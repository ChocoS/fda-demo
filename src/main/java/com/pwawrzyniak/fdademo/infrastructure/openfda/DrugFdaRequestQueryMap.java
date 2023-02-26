package com.pwawrzyniak.fdademo.infrastructure.openfda;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DrugFdaRequestQueryMap {

  private static final int DEFAULT_LIMIT = 5;

  private static final int DEFAULT_SKIP = 0;

  private final String search;

  private final int limit;

  private final int skip;

  private DrugFdaRequestQueryMap(Builder builder) {
    this.search = builder.search;
    this.limit = Optional.ofNullable(builder.limit).orElse(DEFAULT_LIMIT);
    this.skip = Optional.ofNullable(builder.skip).orElse(DEFAULT_SKIP);
  }

  public String getSearch() {
    return search;
  }

  public int getLimit() {
    return limit;
  }

  public int getSkip() {
    return skip;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private static final String SEARCH_PARAM_JOINER = " AND ";

    private static final String MANUFACTURER_NAME_SEARCH_PARAM = "openfda.manufacturer_name";

    private static final String BRAND_NAME_SEARCH_PARAM = "openfda.brand_name";

    private final Map<String, String> searchParams = new HashMap<>();

    private String search;

    private Integer limit;

    private Integer skip;

    private Builder() {
    }

    public Builder withManufacturerName(String manufacturerName) {
      searchParams.put(MANUFACTURER_NAME_SEARCH_PARAM, manufacturerName);
      return this;
    }

    public Builder withBrandName(String brandName) {
      searchParams.put(BRAND_NAME_SEARCH_PARAM, brandName);
      return this;
    }

    public Builder withPageable(Pageable pageable) {
      limit = pageable.getPageSize();
      skip = Math.toIntExact(pageable.getOffset());
      return this;
    }

    public DrugFdaRequestQueryMap build() {
      search = searchParams.entrySet().stream()
          .filter(valueIsNotNull())
          .map(convertToSearchParamWithValue())
          .collect(Collectors.joining(SEARCH_PARAM_JOINER));
      return new DrugFdaRequestQueryMap(this);
    }

    private Function<Map.Entry<String, String>, String> convertToSearchParamWithValue() {
      return entry -> String.format("%s:\"%s\"", entry.getKey(), entry.getValue());
    }

    private Predicate<Map.Entry<String, String>> valueIsNotNull() {
      return entry -> entry.getValue() != null;
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("search", search)
        .append("limit", limit)
        .append("skip", skip)
        .toString();
  }
}