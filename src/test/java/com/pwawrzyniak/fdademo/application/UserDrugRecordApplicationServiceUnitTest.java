package com.pwawrzyniak.fdademo.application;

import com.pwawrzyniak.fdademo.UnitTest;
import com.pwawrzyniak.fdademo.application.dto.CreateUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.SearchUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.UserDrugRecordApplicationView;
import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import com.pwawrzyniak.fdademo.domain.repository.UserDrugRecordApplicationStorageOperations;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDrugRecordApplicationServiceUnitTest extends UnitTest {

  private static final Pageable PAGEABLE = PageRequest.of(3, 2);

  @Mock
  private UserDrugRecordApplicationStorageOperations userDrugRecordApplicationStorageOperations;

  @InjectMocks
  private UserDrugRecordApplicationService userDrugRecordApplicationService;

  @Test
  void shouldCreateUserDrugRecordApplication() {
    // given
    CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest = givenCreateUserDrugRecordApplicationRequest();
    mockUserDrugRecordApplicationStorageSaveOperation();

    // when
    UserDrugRecordApplicationView result = userDrugRecordApplicationService.createUserDrugRecordApplication(createUserDrugRecordApplicationRequest);

    // then
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedUserDrugRecordApplicationView());
    verifyUserDrugRecordApplicationStorageOperationsSaveMock();
  }

  @Test
  void shouldGetUserDrugRecordApplicationByApplicationNumber() {
    // given
    mockUserDrugRecordApplicationStorageFindByApplicationNumberOperation();

    // when
    Optional<UserDrugRecordApplicationView> userDrugRecordApplicationViewOptional = userDrugRecordApplicationService.getUserDrugRecordApplication("test application number");

    // then
    assertThat(userDrugRecordApplicationViewOptional).isPresent();
    assertThat(userDrugRecordApplicationViewOptional.get())
        .usingRecursiveComparison()
        .isEqualTo(expectedUserDrugRecordApplicationView());
  }

  @Test
  void shouldReturnEmptyWhenUserDrugRecordApplicationIsNotFoundByApplicationNumber() {
    // given
    mockUserDrugRecordApplicationStorageFindByApplicationNumberOperationAndReturnEmpty();

    // when
    Optional<UserDrugRecordApplicationView> userDrugRecordApplicationViewOptional = userDrugRecordApplicationService.getUserDrugRecordApplication("test application number");

    // then
    assertThat(userDrugRecordApplicationViewOptional).isEmpty();
  }

  @Test
  void should() {
    // given
    SearchUserDrugRecordApplicationRequest searchUserDrugRecordApplicationRequest = givenSearchUserDrugRecordApplicationRequest();
    mockUserDrugRecordApplicationStorageFindByManufacturerNameAndSubstanceNameOperation();

    // when
    Page<UserDrugRecordApplicationView> userDrugRecordApplicationViewPage = userDrugRecordApplicationService.searchUserDrugRecordApplication(searchUserDrugRecordApplicationRequest, PAGEABLE);

    // then
    assertUserDrugRecordApplicationViewPage(userDrugRecordApplicationViewPage);
  }

  private void assertUserDrugRecordApplicationViewPage(Page<UserDrugRecordApplicationView> userDrugRecordApplicationViewPage) {
    assertThat(userDrugRecordApplicationViewPage).isNotNull();
    assertThat(userDrugRecordApplicationViewPage.getTotalElements()).isEqualTo(8);
    assertThat(userDrugRecordApplicationViewPage.getPageable())
        .usingRecursiveComparison()
        .isEqualTo(PageRequest.of(3, 2));
    assertUserDrugRecordApplicationViewPageContent(userDrugRecordApplicationViewPage.getContent());
  }

  private void assertUserDrugRecordApplicationViewPageContent(List<UserDrugRecordApplicationView> userDrugRecordApplicationViewList) {
    assertThat(userDrugRecordApplicationViewList)
        .usingRecursiveComparison()
        .isEqualTo(List.of(expectedUserDrugRecordApplicationView(), expectedUserDrugRecordApplicationView()));
  }

  private SearchUserDrugRecordApplicationRequest givenSearchUserDrugRecordApplicationRequest() {
    return new SearchUserDrugRecordApplicationRequest("test manufacturer name", "test substance name");
  }

  private void verifyUserDrugRecordApplicationStorageOperationsSaveMock() {
    ArgumentCaptor<UserDrugRecordApplication> userDrugRecordApplicationArgumentCaptor = ArgumentCaptor.forClass(UserDrugRecordApplication.class);
    verify(userDrugRecordApplicationStorageOperations).save(userDrugRecordApplicationArgumentCaptor.capture());
    assertThat(userDrugRecordApplicationArgumentCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(givenUserDrugRecordApplication());
  }

  private UserDrugRecordApplicationView expectedUserDrugRecordApplicationView() {
    return new UserDrugRecordApplicationView("test application number", "test manufacturer name", "test substance name", List.of("product1", "product2"));
  }

  private CreateUserDrugRecordApplicationRequest givenCreateUserDrugRecordApplicationRequest() {
    return new CreateUserDrugRecordApplicationRequest("test application number", "test manufacturer name", "test substance name", List.of("product1", "product2"));
  }

  private UserDrugRecordApplication givenUserDrugRecordApplication() {
    return UserDrugRecordApplication.builder()
        .withApplicationNumber("test application number")
        .withManufacturerName("test manufacturer name")
        .withSubstanceName("test substance name")
        .withProductNames(List.of("product1", "product2"))
        .build();
  }

  private void mockUserDrugRecordApplicationStorageSaveOperation() {
    when(userDrugRecordApplicationStorageOperations.save(any()))
        .thenReturn(givenUserDrugRecordApplication());
  }

  private void mockUserDrugRecordApplicationStorageFindByApplicationNumberOperation() {
    when(userDrugRecordApplicationStorageOperations.findByApplicationNumber(any()))
        .thenReturn(Optional.of(givenUserDrugRecordApplication()));
  }

  private void mockUserDrugRecordApplicationStorageFindByApplicationNumberOperationAndReturnEmpty() {
    when(userDrugRecordApplicationStorageOperations.findByApplicationNumber(any()))
        .thenReturn(Optional.empty());
  }

  private void mockUserDrugRecordApplicationStorageFindByManufacturerNameAndSubstanceNameOperation() {
    when(userDrugRecordApplicationStorageOperations.findByManufacturerNameAndSubstanceName(anyString(), anyString(), any()))
        .thenReturn(new PageImpl<>(List.of(givenUserDrugRecordApplication(), givenUserDrugRecordApplication()), PAGEABLE, 8));
  }
}