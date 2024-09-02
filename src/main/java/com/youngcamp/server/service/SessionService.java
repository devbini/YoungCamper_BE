package com.youngcamp.server.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  private final AuthenticationManager authenticationManager;

  // Security AutoManager 주입
  public SessionService(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
  }

  public String authenticate(String id, String pw, HttpServletRequest request) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(id, pw);

    Authentication authResult = authenticationManager.authenticate(authentication);

    if (authResult.isAuthenticated()) {
      // 인증 처리
      SecurityContextHolder.getContext().setAuthentication(authResult);
      HttpSession session = request.getSession(true);
      return session.getId();

    } else {
      throw new RuntimeException("Invalid credentials");
    }
  }
}
