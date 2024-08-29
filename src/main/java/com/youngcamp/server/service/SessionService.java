<<<<<<< HEAD
package com.youngcamp.server.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
=======
/*
 * **********************************
 * 파일명 : SessionService.java
 * 작성일(수정일) : 2024-08-29
 * 작성자 : 김찬빈
 * -
 * 파일 역할
 * 1. Login & Logout 로직을 담당합니다.
 * **********************************
 */

package com.youngcamp.server.service;

import jakarta.servlet.http.HttpSession;
>>>>>>> 9125423142438ea491d8d14359e302a8c2b0221f
import org.springframework.stereotype.Service;

@Service
public class SessionService {

<<<<<<< HEAD
  private final AuthenticationManager authenticationManager;

  // Security AutoManager 주입
  public SessionService(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
  }

  public String authenticate(String id, String pw, HttpServletRequest request) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(id, pw);

    Authentication authResult = authenticationManager.authenticate(authentication);

    if (authResult.isAuthenticated()) {
      // 인증처리
      SecurityContextHolder.getContext().setAuthentication(authResult);
      HttpSession session = request.getSession(true);
      return session.getId();

    } else {
      throw new RuntimeException("Invalid credentials");
    }
=======
  // 로그인 메서드
  public boolean login(String id, String password) {
    boolean isSuccess = false;

    // DB 조회부가 들어가야 함, 지금은 테스트용으로 root root 고정 (2024-08-29)
    isSuccess = "root".equals(id) && "root".equals(password);

    return isSuccess;
  }

  // 로그아웃 메서드
  public void logout(HttpSession session) {
    session.invalidate();
>>>>>>> 9125423142438ea491d8d14359e302a8c2b0221f
  }
}
