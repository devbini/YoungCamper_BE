package com.youngcamp.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
public class CookieConfig {

  @Bean
  public HttpSessionIdResolver httpSessionIdResolver() {
    CookieHttpSessionIdResolver resolver = new CookieHttpSessionIdResolver();
    DefaultCookieSerializer Serializer = new DefaultCookieSerializer();

    Serializer.setCookieName("ADMINID");
    Serializer.setCookiePath("/");
    Serializer.setCookieMaxAge(600);
    Serializer.setUseHttpOnlyCookie(true);
    Serializer.setSameSite("Lax");

    resolver.setCookieSerializer(Serializer);
    return resolver;
  }
}
