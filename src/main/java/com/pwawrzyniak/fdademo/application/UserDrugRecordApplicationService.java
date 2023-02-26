package com.pwawrzyniak.fdademo.application;

import com.pwawrzyniak.fdademo.application.dto.CreateUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.SearchUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.UserDrugRecordApplicationView;
import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import com.pwawrzyniak.fdademo.domain.repository.UserDrugRecordApplicationStorageOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
class UserDrugRecordApplicationService implements UserDrugRecordApplicationOperations {

  private final UserDrugRecordApplicationStorageOperations userDrugRecordApplicationStorageOperations;

  UserDrugRecordApplicationService(UserDrugRecordApplicationStorageOperations userDrugRecordApplicationStorageOperations) {
    this.userDrugRecordApplicationStorageOperations = userDrugRecordApplicationStorageOperations;
  }

  @Override
  public UserDrugRecordApplicationView createUserDrugRecordApplication(CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest) {
    return UserDrugRecordApplicationView.fromUserDrugRecordApplicationDto(userDrugRecordApplicationStorageOperations.save(createUserDrugRecordApplicationRequest.toUserDrugRecordApplication()));
  }

  @Override
  public Optional<UserDrugRecordApplicationView> getUserDrugRecordApplication(String applicationNumber) {
    return userDrugRecordApplicationStorageOperations.findByApplicationNumber(applicationNumber)
        .map(UserDrugRecordApplicationView::fromUserDrugRecordApplicationDto);
  }

  @Override
  public Page<UserDrugRecordApplicationView> searchUserDrugRecordApplication(SearchUserDrugRecordApplicationRequest searchUserDrugRecordApplicationRequest, Pageable pageable) {
    Page<UserDrugRecordApplication> userDrugRecordApplicationPage = userDrugRecordApplicationStorageOperations.findByManufacturerNameAndSubstanceName(searchUserDrugRecordApplicationRequest.getManufacturerName(),
        searchUserDrugRecordApplicationRequest.getSubstanceName(),
        pageable);
    return new PageImpl<>(userDrugRecordApplicationPage.stream().map(UserDrugRecordApplicationView::fromUserDrugRecordApplicationDto).collect(Collectors.toUnmodifiableList()),
        pageable, userDrugRecordApplicationPage.getNumberOfElements());
  }
}