package com.pwawrzyniak.fdademo.infrastructure.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@EnableMongoAuditing
@Configuration
class MongoConfiguration {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final MongoTemplate mongoTemplate;

  private final MongoConverter mongoConverter;

  MongoConfiguration(MongoTemplate mongoTemplate, MongoConverter mongoConverter) {
    this.mongoTemplate = mongoTemplate;
    this.mongoConverter = mongoConverter;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void initIndexes() {
    logger.info("Mongo indexes initialization started");
    MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoConverter.getMappingContext();
    if (!(mappingContext instanceof MongoMappingContext)) {
      logger.warn("Mongo mapping context not found, cannot initialize Mongo indexes");
      return;
    }
    MongoMappingContext mongoMappingContext = (MongoMappingContext) mappingContext;
    MongoPersistentEntityIndexResolver mongoPersistentEntityIndexResolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
    for (MongoPersistentEntity<?> mongoPersistentEntity : mongoMappingContext.getPersistentEntities()) {
      Class<?> clazz = mongoPersistentEntity.getType();
      if (clazz.isAnnotationPresent(Document.class)) {
        IndexOperations indexOperations = mongoTemplate.indexOps(clazz);
        mongoPersistentEntityIndexResolver.resolveIndexFor(clazz)
            .forEach(indexOperations::ensureIndex);
      }
    }
    logger.info("Finished initializing Mongo indexes");
  }
}