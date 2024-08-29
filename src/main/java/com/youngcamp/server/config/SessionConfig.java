/*
 * **********************************
 * 파일명 : SessionConfig.java
 * 작성일(수정일) : 2024-08-28
 * 작성자 : 김찬빈
 * -
 * 파일 역할
 * 1. BE Admin 권한 세션 타임아웃을 설정하는 클래스
 * 2. ServletContextInitializer 인터페이스를 통해 타임아웃 '10분' 설정
 * ***********************************
 */

package com.youngcamp.server.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

  @Bean
  public ServletContextInitializer initializer() {
    return new ServletContextInitializer() {
      @Override // onStartup Override, 예약 메서드에서 앱 초기 시작 시 타임아웃을 설정합니다.
      public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.setSessionTimeout(10);
      }
    };
  }
}
