package com.pwawrzyniak.fdademo.infrastructure.mongo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

class UserDrugRecordApplicationCustomRepositoryImpl implements UserDrugRecordApplicationCustomRepository {

  private static final String MANUFACTURER_NAME_FIELD = "manufacturerName";

  private static final String SUBSTANCE_NAME_FIELD = "substanceName";

  private final MongoTemplate mongoTemplate;

  UserDrugRecordApplicationCustomRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Page<UserDrugRecordApplicationEntity> findByManufacturerNameAndSubstanceName(String manufacturerName, String substanceName, Pageable pageable) {
    Query query = new Query().with(pageable).addCriteria(buildCriteria(manufacturerName, substanceName));
    return PageableExecutionUtils.getPage(mongoTemplate.find(query, UserDrugRecordApplicationEntity.class),
        pageable, () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), UserDrugRecordApplicationEntity.class));
  }

  private Criteria buildCriteria(String manufacturerName, String substanceName) {
    List<Criteria> result = new ArrayList<>();
    if (StringUtils.isNotBlank(manufacturerName)) {
      result.add(Criteria.where(MANUFACTURER_NAME_FIELD).is(manufacturerName));
    }
    if (StringUtils.isNotBlank(substanceName)) {
      result.add(Criteria.where(SUBSTANCE_NAME_FIELD).is(substanceName));
    }
    if (result.isEmpty()) {
      return new Criteria();
    }
    if (result.size() == 1) {
      return result.get(0);
    }
    return new Criteria().andOperator(result.toArray(new Criteria[0]));
  }
}