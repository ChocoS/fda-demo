package com.pwawrzyniak.fdademo.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserDrugRecordApplicationMongoRepository extends MongoRepository<UserDrugRecordApplicationEntity, String>, UserDrugRecordApplicationCustomRepository {

  Optional<UserDrugRecordApplicationEntity> findByApplicationNumber(String applicationNumber);
}