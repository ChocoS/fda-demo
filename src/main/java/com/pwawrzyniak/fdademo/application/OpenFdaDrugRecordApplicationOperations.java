package com.pwawrzyniak.fdademo.application;

import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OpenFdaDrugRecordApplicationOperations {

  Page<DrugFda> search(String manufacturerName, String brandName, Pageable pageable);
}