package com.youngcamp.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngcamp.server.security.XSSCharacterEscapes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.getFactory().setCharacterEscapes(new XSSCharacterEscapes());
    return objectMapper;
  }
}
