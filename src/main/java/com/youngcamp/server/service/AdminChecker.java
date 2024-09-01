package com.youngcamp.server.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AdminChecker {
  private static final String ADMIN_ROLE = "ADMIN";

  public boolean isAdmin() {
    Authentication current = SecurityContextHolder.getContext().getAuthentication();

    return current != null
        && current.isAuthenticated()
        && current.getAuthorities().stream()
            .anyMatch(list -> ADMIN_ROLE.equals(list.getAuthority()));
  }
}
