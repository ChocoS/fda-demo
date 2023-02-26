package com.pwawrzyniak.fdademo.infrastructure.openfda;

import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFdaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "OpenFdaFeignClient", url = "${application.open-fda.base-url}", configuration = OpenFdaFeignClientConfiguration.class)
public interface OpenFdaFeignClient {

  @GetMapping("/drug/drugsfda.json")
  DrugFdaResponse search(@SpringQueryMap DrugFdaRequestQueryMap drugFdaRequestQueryMap) throws OpenFdaFeignClientException;
}