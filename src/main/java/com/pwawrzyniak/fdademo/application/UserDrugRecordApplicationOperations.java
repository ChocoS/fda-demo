package com.pwawrzyniak.fdademo.application;

import com.pwawrzyniak.fdademo.application.dto.CreateUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.SearchUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.UserDrugRecordApplicationView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserDrugRecordApplicationOperations {

  UserDrugRecordApplicationView createUserDrugRecordApplication(CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest);

  Optional<UserDrugRecordApplicationView> getUserDrugRecordApplication(String applicationNumber);

  Page<UserDrugRecordApplicationView> searchUserDrugRecordApplication(SearchUserDrugRecordApplicationRequest searchUserDrugRecordApplicationRequest, Pageable pageable);
}