package com.youngcamp.server.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AdminChecker {
  private static final String ADMIN_ROLE = "ROLE_ADMIN";

  public boolean isAdmin() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .anyMatch(authority -> ADMIN_ROLE.equals(authority.getAuthority()));
  }
}
