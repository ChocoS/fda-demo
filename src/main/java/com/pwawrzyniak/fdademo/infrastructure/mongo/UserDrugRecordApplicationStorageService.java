package com.pwawrzyniak.fdademo.infrastructure.mongo;

import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import com.pwawrzyniak.fdademo.domain.repository.UserDrugRecordApplicationStorageOperations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
class UserDrugRecordApplicationStorageService implements UserDrugRecordApplicationStorageOperations {

  private final UserDrugRecordApplicationMongoRepository userDrugRecordApplicationMongoRepository;

  UserDrugRecordApplicationStorageService(UserDrugRecordApplicationMongoRepository userDrugRecordApplicationMongoRepository) {
    this.userDrugRecordApplicationMongoRepository = userDrugRecordApplicationMongoRepository;
  }

  @Override
  public UserDrugRecordApplication save(UserDrugRecordApplication userDrugRecordApplication) {
    UserDrugRecordApplicationEntity userDrugRecordApplicationEntity = UserDrugRecordApplicationEntity.fromUserDrugRecordApplication(userDrugRecordApplication);
    try {
      return userDrugRecordApplicationMongoRepository.save(userDrugRecordApplicationEntity)
          .toUserDrugRecordApplication();
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new UserDrugRecordApplicationStorageServiceDuplicateKeyException(duplicateKeyException.getMessage());
    }
  }

  @Override
  public Optional<UserDrugRecordApplication> findByApplicationNumber(String applicationNumber) {
    return userDrugRecordApplicationMongoRepository.findByApplicationNumber(applicationNumber)
        .map(UserDrugRecordApplicationEntity::toUserDrugRecordApplication);
  }

  @Override
  public Page<UserDrugRecordApplication> findByManufacturerNameAndSubstanceName(String manufacturerName, String substanceName, Pageable pageable) {
    Page<UserDrugRecordApplicationEntity> userDrugRecordApplicationEntityPage = userDrugRecordApplicationMongoRepository.findByManufacturerNameAndSubstanceName(manufacturerName, substanceName, pageable);
    return new PageImpl<>(userDrugRecordApplicationEntityPage.stream().map(UserDrugRecordApplicationEntity::toUserDrugRecordApplication).collect(Collectors.toUnmodifiableList()),
        pageable, userDrugRecordApplicationEntityPage.getTotalElements());
  }
}