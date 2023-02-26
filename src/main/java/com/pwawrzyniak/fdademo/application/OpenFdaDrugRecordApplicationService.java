package com.pwawrzyniak.fdademo.application;

import com.pwawrzyniak.fdademo.infrastructure.openfda.DrugFdaRequestQueryMap;
import com.pwawrzyniak.fdademo.infrastructure.openfda.OpenFdaFeignClient;
import com.pwawrzyniak.fdademo.infrastructure.openfda.OpenFdaFeignClientException;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFda;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFdaResponse;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.Meta;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.MetaResults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
class OpenFdaDrugRecordApplicationService implements OpenFdaDrugRecordApplicationOperations {

  private final OpenFdaFeignClient openFdaFeignClient;

  OpenFdaDrugRecordApplicationService(OpenFdaFeignClient openFdaFeignClient) {
    this.openFdaFeignClient = openFdaFeignClient;
  }

  @Override
  public Page<DrugFda> search(String manufacturerName, String brandName, Pageable pageable) {
    DrugFdaRequestQueryMap drugFdaRequestQueryMap = prepareDrugFdaRequestQueryMap(manufacturerName, brandName, pageable);
    DrugFdaResponse drugFdaResponse;
    try {
      drugFdaResponse = openFdaFeignClient.search(drugFdaRequestQueryMap);
    } catch (OpenFdaFeignClientException exception) {
      String message = String.format("Open FDA feign client call failed with error message: '%s'", exception.getMessage());
      if (HttpStatus.NOT_FOUND.equals(exception.getHttpStatus())) {
        throw new OpenFdaDrugRecordApplicationServiceNotFoundException(message);
      }
      throw new OpenFdaDrugRecordApplicationServiceException(message);
    }
    return new PageImpl<>(drugFdaResponse.getResults(), pageable, getTotalNumberOfRecords(drugFdaResponse));
  }

  private DrugFdaRequestQueryMap prepareDrugFdaRequestQueryMap(String manufacturerName, String brandName, Pageable pageable) {
    return DrugFdaRequestQueryMap.builder()
        .withManufacturerName(manufacturerName)
        .withBrandName(brandName)
        .withPageable(pageable)
        .build();
  }

  private long getTotalNumberOfRecords(DrugFdaResponse drugFdaResponse) {
    return drugFdaResponse.getMeta()
        .flatMap(Meta::getResults)
        .flatMap(MetaResults::getTotal)
        .orElseThrow(() -> new OpenFdaDrugRecordApplicationServiceException("Missing total number of found records"));
  }
}