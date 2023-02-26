package com.pwawrzyniak.fdademo.infrastructure.mongo;

import com.pwawrzyniak.fdademo.IntegrationTest;
import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import com.pwawrzyniak.fdademo.domain.repository.UserDrugRecordApplicationStorageOperations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserDrugRecordApplicationStorageServiceIntegrationTest extends IntegrationTest {

  private static final String USER_DRUG_RECORD_APPLICATION_COLLECTION_NAME = "applications";

  private static final Query EMPTY_QUERY = new Query();

  private static final UserDrugRecordApplication USER_DRUG_RECORD_APPLICATION = UserDrugRecordApplication.builder()
      .withApplicationNumber("test application number")
      .withManufacturerName("test manufacturer name")
      .withSubstanceName("test substance name")
      .withProductNames(List.of("product1", "product2"))
      .build();

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private UserDrugRecordApplicationStorageOperations userDrugRecordApplicationStorageOperations;

  @AfterEach
  void cleanupTestData() {
    mongoTemplate.remove(EMPTY_QUERY, USER_DRUG_RECORD_APPLICATION_COLLECTION_NAME);
  }

  @Test
  void shouldSaveUserDrugRecordApplication() {
    // given
    long storedUserDrugRecordApplicationsCountBeforeTest = countStoredUserDrugRecordApplications();

    // when
    userDrugRecordApplicationStorageOperations.save(USER_DRUG_RECORD_APPLICATION);

    // then
    long storedUserDrugRecordApplicationsCountAfterTest = countStoredUserDrugRecordApplications();
    assertThat(storedUserDrugRecordApplicationsCountBeforeTest).isZero();
    assertThat(storedUserDrugRecordApplicationsCountAfterTest).isOne();
    assertStoredEntity();
  }

  @Test
  void shouldThrowExceptionWhenSavingUserDrugRecordApplicationThatAlreadyExists() {
    // given
    storeUserDrugRecordApplication();
    long storedUserDrugRecordApplicationsCountBeforeTest = countStoredUserDrugRecordApplications();

    // when - then
    assertThatThrownBy(() -> userDrugRecordApplicationStorageOperations.save(USER_DRUG_RECORD_APPLICATION))
        .isExactlyInstanceOf(UserDrugRecordApplicationStorageServiceDuplicateKeyException.class);
    long storedUserDrugRecordApplicationsCountAfterTest = countStoredUserDrugRecordApplications();
    assertThat(storedUserDrugRecordApplicationsCountBeforeTest)
        .isEqualTo(storedUserDrugRecordApplicationsCountAfterTest)
        .isOne();
  }

  @Test
  void shouldFindUserDrugRecordApplicationByApplicationNumber() {
    // given
    storeUserDrugRecordApplication();

    // when
    Optional<UserDrugRecordApplication> userDrugRecordApplication = userDrugRecordApplicationStorageOperations.findByApplicationNumber("test application number");

    // then
    assertThat(userDrugRecordApplication).isPresent();
    assertThat(userDrugRecordApplication.get())
        .usingRecursiveComparison()
        .isEqualTo(USER_DRUG_RECORD_APPLICATION);
  }

  @Test
  void shouldReturnEmptyWhenUserDrugRecordApplicationIsNotFoundByApplicationNumber() {
    // given
    storeUserDrugRecordApplication();

    // when
    Optional<UserDrugRecordApplication> userDrugRecordApplication = userDrugRecordApplicationStorageOperations.findByApplicationNumber("invalid application number");

    // then
    assertThat(userDrugRecordApplication).isEmpty();
  }

  @Test
  void shouldFindUserDrugRecordApplicationsByManufacturerNameAndSubstanceName() {
    // given
    storeMultipleUserDrugRecordApplications();

    // when
    Page<UserDrugRecordApplication> userDrugRecordApplicationPage = userDrugRecordApplicationStorageOperations.findByManufacturerNameAndSubstanceName("manufacturerName2", "substanceName2", PageRequest.of(1, 2));

    // then
    assertThat(userDrugRecordApplicationPage.getPageable()).isEqualTo(PageRequest.of(1, 2));
    assertThat(userDrugRecordApplicationPage.getTotalElements()).isEqualTo(3);
    assertThat(userDrugRecordApplicationPage.getContent())
        .usingRecursiveComparison()
        .isEqualTo(List.of(userDrugRecordApplication("applicationNumber8", "manufacturerName2", "substanceName2")));
  }

  @Test
  void shouldFindUserDrugRecordApplicationsByManufacturerName() {
    // given
    storeMultipleUserDrugRecordApplications();

    // when
    Page<UserDrugRecordApplication> userDrugRecordApplicationPage = userDrugRecordApplicationStorageOperations.findByManufacturerNameAndSubstanceName("manufacturerName2", null, PageRequest.of(1, 2));

    // then
    assertThat(userDrugRecordApplicationPage.getPageable()).isEqualTo(PageRequest.of(1, 2));
    assertThat(userDrugRecordApplicationPage.getTotalElements()).isEqualTo(5);
    assertThat(userDrugRecordApplicationPage.getContent())
        .usingRecursiveComparison()
        .isEqualTo(List.of(userDrugRecordApplication("applicationNumber8", "manufacturerName2", "substanceName2"),
            userDrugRecordApplication("applicationNumber9", "manufacturerName2", "substanceName3")));
  }

  @Test
  void shouldFindAllUserDrugRecordApplicationsWhenSearchCriteriaAreEmpty() {
    // given
    storeMultipleUserDrugRecordApplications();

    // when
    Page<UserDrugRecordApplication> userDrugRecordApplicationPage = userDrugRecordApplicationStorageOperations.findByManufacturerNameAndSubstanceName(null, null, PageRequest.of(1, 2));

    // then
    assertThat(userDrugRecordApplicationPage.getPageable()).isEqualTo(PageRequest.of(1, 2));
    assertThat(userDrugRecordApplicationPage.getTotalElements()).isEqualTo(10);
    assertThat(userDrugRecordApplicationPage.getContent())
        .usingRecursiveComparison()
        .isEqualTo(List.of(userDrugRecordApplication("applicationNumber3", "manufacturerName1", "substanceName1"),
            userDrugRecordApplication("applicationNumber4", "manufacturerName1", "substanceName2")));
  }

  private void storeMultipleUserDrugRecordApplications() {
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber1", "manufacturerName1", "substanceName1"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber2", "manufacturerName1", "substanceName1"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber3", "manufacturerName1", "substanceName1"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber4", "manufacturerName1", "substanceName2"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber5", "manufacturerName1", "substanceName2"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber6", "manufacturerName2", "substanceName2"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber7", "manufacturerName2", "substanceName2"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber8", "manufacturerName2", "substanceName2"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber9", "manufacturerName2", "substanceName3"));
    userDrugRecordApplicationStorageOperations.save(userDrugRecordApplication("applicationNumber10", "manufacturerName2", "substanceName3"));
  }

  private UserDrugRecordApplication userDrugRecordApplication(String applicationNumber, String manufacturerName, String substanceName) {
    return UserDrugRecordApplication.builder()
        .withApplicationNumber(applicationNumber)
        .withManufacturerName(manufacturerName)
        .withSubstanceName(substanceName)
        .build();
  }

  private void storeUserDrugRecordApplication() {
    userDrugRecordApplicationStorageOperations.save(USER_DRUG_RECORD_APPLICATION);
  }

  private void assertStoredEntity() {
    List<UserDrugRecordApplicationEntity> userDrugRecordApplicationEntities = mongoTemplate.find(EMPTY_QUERY, UserDrugRecordApplicationEntity.class);
    assertThat(userDrugRecordApplicationEntities).hasSize(1);
    UserDrugRecordApplicationEntity userDrugRecordApplicationEntity = userDrugRecordApplicationEntities.get(0);
    assertThat(userDrugRecordApplicationEntity)
        .usingRecursiveComparison()
        .ignoringFields("id", "createdAt")
        .isEqualTo(new UserDrugRecordApplicationEntity(null, "test application number", "test manufacturer name", "test substance name", List.of("product1", "product2"), null));
    assertThat(userDrugRecordApplicationEntity.getId()).isNotNull();
    assertThat(userDrugRecordApplicationEntity.getCreatedAt()).isNotNull();
  }

  private long countStoredUserDrugRecordApplications() {
    return mongoTemplate.count(EMPTY_QUERY, USER_DRUG_RECORD_APPLICATION_COLLECTION_NAME);
  }
}