package com.youngcamp.server.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder().group("api").pathsToMatch("/api/**").build();
  }
}
