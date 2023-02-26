package com.pwawrzyniak.fdademo.application.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;

class JacksonConfiguration {

  JacksonConfiguration(ObjectMapper objectMapper) {
    objectMapper.registerModules(new PageJacksonModule(), new SortJacksonModule());
  }
}