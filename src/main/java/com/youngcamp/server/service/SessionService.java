package com.youngcamp.server.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  private final AuthenticationManager authenticationManager;

  public SessionService(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
  }

  public String authenticate(String id, String pw, HttpServletRequest request) {
    try {
      Authentication authentication = new UsernamePasswordAuthenticationToken(id, pw);
      Authentication authResult = authenticationManager.authenticate(authentication);

      // SecurityContext 설정 및 저장
      SecurityContext context = SecurityContextHolder.getContext();
      context.setAuthentication(authResult);
      HttpSession session = request.getSession(true);
      session.setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

      return session.getId();

    } catch (Exception e) {
      throw new RuntimeException("인증 실패", e);
    }
  }
}
