package com.pwawrzyniak.fdademo.application.rest;

import com.pwawrzyniak.fdademo.IntegrationTest;
import com.pwawrzyniak.fdademo.application.UserDrugRecordApplicationOperations;
import com.pwawrzyniak.fdademo.application.dto.CreateUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.CreateUserDrugRecordApplicationResponse;
import com.pwawrzyniak.fdademo.application.dto.ErrorResponse;
import com.pwawrzyniak.fdademo.application.dto.SearchUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.UserDrugRecordApplicationView;
import com.pwawrzyniak.fdademo.infrastructure.mongo.UserDrugRecordApplicationStorageServiceDuplicateKeyException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@Import(JacksonConfiguration.class)
class UserDrugRecordApplicationControllerIntegrationTest extends IntegrationTest {

  private static final String APPLICATION_NUMBER = "test application number";

  private static final SearchUserDrugRecordApplicationRequest SEARCH_USER_DRUG_RECORD_APPLICATION_REQUEST =
      new SearchUserDrugRecordApplicationRequest("test manufacturer name", "test substance name");

  private static final Pageable PAGEABLE = PageRequest.of(3, 2);

  @MockBean
  private UserDrugRecordApplicationOperations userDrugRecordApplicationOperations;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void shouldCreateDrugRecordApplication() {
    // given
    CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest = givenCreateUserDrugRecordApplicationRequest();
    mockCreateUserDrugRecordApplication();

    // when
    ResponseEntity<CreateUserDrugRecordApplicationResponse> response = callToCreateDrugRecordApplication(createUserDrugRecordApplicationRequest);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(new CreateUserDrugRecordApplicationResponse(APPLICATION_NUMBER));
    verifyCreateUserDrugApplicationMock(createUserDrugRecordApplicationRequest);
  }

  @Test
  void shouldReturnConflictWhenDrugRecordApplicationAlreadyExists() {
    // given
    CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest = givenCreateUserDrugRecordApplicationRequest();
    when(userDrugRecordApplicationOperations.createUserDrugRecordApplication(any()))
        .thenThrow(new UserDrugRecordApplicationStorageServiceDuplicateKeyException("Duplicate key"));

    // when
    ResponseEntity<ErrorResponse> response = callToCreateDrugRecordApplicationAndExpectError(createUserDrugRecordApplicationRequest);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(new ErrorResponse("User drug record application already exists"));
    verifyCreateUserDrugApplicationMock(createUserDrugRecordApplicationRequest);
  }

  @Test
  void shouldReturnBadRequestWhenApplicationNumberIsMissing() {
    // given
    CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest = givenCreateUserDrugRecordApplicationRequestWithoutApplicationNumber();

    // when
    ResponseEntity<ErrorResponse> response = callToCreateDrugRecordApplicationAndExpectError(createUserDrugRecordApplicationRequest);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(new ErrorResponse("Request is not valid"));
    verifyNoInteractionsWithMock();
  }

  @Test
  void shouldGetDrugRecordApplicationByApplicationNumber() {
    // given
    mockGetUserDrugRecordApplicationByApplicationNumber();

    // when
    ResponseEntity<UserDrugRecordApplicationView> response = callToGetDrugRecordApplicationByApplicationNumber();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(givenUserDrugRecordApplicationView());
    verifyGetUserDrugApplicationByApplicationNumberMock();
  }

  @Test
  void shouldNotFindDrugRecordApplicationByApplicationNumber() {
    // given
    mockGetUserDrugRecordApplicationByApplicationNumberAndReturnEmpty();

    // when
    ResponseEntity<ErrorResponse> response = callToGetDrugRecordApplicationByApplicationNumberAndExpectError();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(new ErrorResponse("User drug record application was not found"));
    verifyGetUserDrugApplicationByApplicationNumberMock();
  }

  @Test
  void shouldFindDrugRecordApplications() {
    // given
    mockFindUserDrugRecordApplications();

    // when
    ResponseEntity<Page<UserDrugRecordApplicationView>> response = callToSearchDrugRecordApplications();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertUserDrugRecordApplicationViewPage(response.getBody());
    verifyFindUserDrugApplicationsMock();
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
        .isEqualTo(List.of(givenUserDrugRecordApplicationView(), givenUserDrugRecordApplicationView()));
  }

  private void verifyCreateUserDrugApplicationMock(CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest) {
    ArgumentCaptor<CreateUserDrugRecordApplicationRequest> userDrugRecordApplicationRequestArgumentCaptor = ArgumentCaptor.forClass(CreateUserDrugRecordApplicationRequest.class);
    verify(userDrugRecordApplicationOperations).createUserDrugRecordApplication(userDrugRecordApplicationRequestArgumentCaptor.capture());
    assertThat(userDrugRecordApplicationRequestArgumentCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(createUserDrugRecordApplicationRequest);
  }

  private void verifyGetUserDrugApplicationByApplicationNumberMock() {
    verify(userDrugRecordApplicationOperations).getUserDrugRecordApplication(APPLICATION_NUMBER);
  }

  private void verifyFindUserDrugApplicationsMock() {
    ArgumentCaptor<SearchUserDrugRecordApplicationRequest> searchUserDrugRecordApplicationRequestArgumentCaptor = ArgumentCaptor.forClass(SearchUserDrugRecordApplicationRequest.class);
    verify(userDrugRecordApplicationOperations).searchUserDrugRecordApplication(searchUserDrugRecordApplicationRequestArgumentCaptor.capture(), eq(PAGEABLE));
    assertThat(searchUserDrugRecordApplicationRequestArgumentCaptor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(SEARCH_USER_DRUG_RECORD_APPLICATION_REQUEST);
  }

  private void mockCreateUserDrugRecordApplication() {
    when(userDrugRecordApplicationOperations.createUserDrugRecordApplication(any()))
        .thenReturn(givenUserDrugRecordApplicationView());
  }

  private void mockGetUserDrugRecordApplicationByApplicationNumber() {
    when(userDrugRecordApplicationOperations.getUserDrugRecordApplication(APPLICATION_NUMBER))
        .thenReturn(Optional.of(givenUserDrugRecordApplicationView()));
  }

  private void mockGetUserDrugRecordApplicationByApplicationNumberAndReturnEmpty() {
    when(userDrugRecordApplicationOperations.getUserDrugRecordApplication(APPLICATION_NUMBER))
        .thenReturn(Optional.empty());
  }

  private void mockFindUserDrugRecordApplications() {
    when(userDrugRecordApplicationOperations.searchUserDrugRecordApplication(any(), any()))
        .thenReturn(new PageImpl<>(List.of(givenUserDrugRecordApplicationView(), givenUserDrugRecordApplicationView()), PAGEABLE, 8));
  }

  private void verifyNoInteractionsWithMock() {
    verifyNoInteractions(userDrugRecordApplicationOperations);
  }

  private CreateUserDrugRecordApplicationRequest givenCreateUserDrugRecordApplicationRequest() {
    return new CreateUserDrugRecordApplicationRequest(APPLICATION_NUMBER,
        "test manufacturer name",
        "test substance name",
        List.of("product1", "product2"));
  }

  private CreateUserDrugRecordApplicationRequest givenCreateUserDrugRecordApplicationRequestWithoutApplicationNumber() {
    return new CreateUserDrugRecordApplicationRequest(null,
        "test manufacturer name",
        "test substance name",
        List.of("product1", "product2"));
  }

  private UserDrugRecordApplicationView givenUserDrugRecordApplicationView() {
    return new UserDrugRecordApplicationView(APPLICATION_NUMBER, null, null, null);
  }

  private ResponseEntity<CreateUserDrugRecordApplicationResponse> callToCreateDrugRecordApplication(CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest) {
    return testRestTemplate.exchange("/v1/user-dra", HttpMethod.POST,
        new HttpEntity<>(createUserDrugRecordApplicationRequest), CreateUserDrugRecordApplicationResponse.class);
  }

  private ResponseEntity<ErrorResponse> callToCreateDrugRecordApplicationAndExpectError(CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest) {
    return testRestTemplate.exchange("/v1/user-dra", HttpMethod.POST,
        new HttpEntity<>(createUserDrugRecordApplicationRequest), ErrorResponse.class);
  }

  private ResponseEntity<UserDrugRecordApplicationView> callToGetDrugRecordApplicationByApplicationNumber() {
    return testRestTemplate.exchange("/v1/user-dra/{applicationNumber}", HttpMethod.GET,
        null, UserDrugRecordApplicationView.class, APPLICATION_NUMBER);
  }

  private ResponseEntity<ErrorResponse> callToGetDrugRecordApplicationByApplicationNumberAndExpectError() {
    return testRestTemplate.exchange("/v1/user-dra/{applicationNumber}", HttpMethod.GET,
        null, ErrorResponse.class, APPLICATION_NUMBER);
  }

  private ResponseEntity<Page<UserDrugRecordApplicationView>> callToSearchDrugRecordApplications() {
    return testRestTemplate.exchange("/v1/user-dra/actions/search?page={page}&size={size}", HttpMethod.POST,
        new HttpEntity<>(SEARCH_USER_DRUG_RECORD_APPLICATION_REQUEST), new ParameterizedTypeReference<>() {
        }, 3, 2);
  }
}