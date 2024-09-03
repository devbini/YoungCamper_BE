package com.youngcamp.server.aspect;

import com.youngcamp.server.service.AdminChecker;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AdminAspect {

  private final AdminChecker adminChecker;

  public AdminAspect(AdminChecker adminChecker) {
    this.adminChecker = adminChecker;
  }

  @Pointcut("@annotation(com.youngcamp.server.annotation.AdminOnly)")
  public void adminOnlyMethods() {}

  @Before("adminOnlyMethods()")
  public void checkAdminAccess() {
    if (!adminChecker.isAdmin()) {
      throw new AccessDeniedException("권한이 부족합니다.");
    }
  }
}
