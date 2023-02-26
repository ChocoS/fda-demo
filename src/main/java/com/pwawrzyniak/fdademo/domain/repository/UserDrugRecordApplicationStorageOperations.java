package com.pwawrzyniak.fdademo.domain.repository;

import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserDrugRecordApplicationStorageOperations {

  UserDrugRecordApplication save(UserDrugRecordApplication userDrugRecordApplication);

  Optional<UserDrugRecordApplication> findByApplicationNumber(String applicationNumber);

  Page<UserDrugRecordApplication> findByManufacturerNameAndSubstanceName(String manufacturerName, String substanceName, Pageable pageable);
}