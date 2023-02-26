package com.pwawrzyniak.fdademo.infrastructure.mongo;

import com.pwawrzyniak.fdademo.domain.UserDrugRecordApplication;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("applications")
@TypeAlias("UserDrugRecordApplicationEntity")
@CompoundIndex(name = "manufacturer_name_substance_name_index", def = "{'manufacturerName': 1, 'substanceName': 1}")
class UserDrugRecordApplicationEntity {

  @Id
  private final String id;

  @Indexed(name = "application_name_index", unique = true)
  private final String applicationNumber;

  private final String manufacturerName;

  private final String substanceName;

  private final List<String> productNames;

  @CreatedDate
  private final Instant createdAt;

  UserDrugRecordApplicationEntity(String id,
                                  String applicationNumber,
                                  String manufacturerName,
                                  String substanceName,
                                  List<String> productNames,
                                  Instant createdAt) {
    this.id = id;
    this.applicationNumber = applicationNumber;
    this.manufacturerName = manufacturerName;
    this.substanceName = substanceName;
    this.productNames = productNames;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public String getSubstanceName() {
    return substanceName;
  }

  public List<String> getProductNames() {
    return productNames;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public static UserDrugRecordApplicationEntity fromUserDrugRecordApplication(UserDrugRecordApplication userDrugRecordApplication) {
    return new UserDrugRecordApplicationEntity(null,
        userDrugRecordApplication.getApplicationNumber(),
        userDrugRecordApplication.getManufacturerName(),
        userDrugRecordApplication.getSubstanceName(),
        userDrugRecordApplication.getProductNames(),
        null);
  }

  public UserDrugRecordApplication toUserDrugRecordApplication() {
    return UserDrugRecordApplication.builder()
        .withApplicationNumber(applicationNumber)
        .withManufacturerName(manufacturerName)
        .withSubstanceName(substanceName)
        .withProductNames(productNames)
        .build();
  }
}