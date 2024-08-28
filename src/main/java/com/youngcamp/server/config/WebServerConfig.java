package com.youngcamp.server.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerConfig {

  @Bean
  public TomcatServletWebServerFactory servletContainer() {
    return new TomcatServletWebServerFactory();
  }
}
