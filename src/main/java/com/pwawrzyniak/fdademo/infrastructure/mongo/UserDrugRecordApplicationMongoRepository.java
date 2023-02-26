package com.pwawrzyniak.fdademo.infrastructure.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserDrugRecordApplicationMongoRepository extends MongoRepository<UserDrugRecordApplicationEntity, String> {

  Optional<UserDrugRecordApplicationEntity> findByApplicationNumber(String applicationNumber);

  Page<UserDrugRecordApplicationEntity> findByManufacturerNameAndSubstanceName(String manufacturerName, String substanceName, Pageable pageable);
}