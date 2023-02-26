package com.pwawrzyniak.fdademo.infrastructure.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface UserDrugRecordApplicationCustomRepository {

  Page<UserDrugRecordApplicationEntity> findByManufacturerNameAndSubstanceName(String manufacturerName, String substanceName, Pageable pageable);
}