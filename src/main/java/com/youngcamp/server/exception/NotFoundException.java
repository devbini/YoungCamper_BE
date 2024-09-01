package com.youngcamp.server.exception;

public class NotFoundException extends RuntimeException {

  private final String resourceType;
  private final Object resourceId;

  // 생성자: 자원 타입과 ID를 추가로 받음
  public NotFoundException(String resourceType, Object resourceId, String message) {
    super(message);
    this.resourceType = resourceType;
    this.resourceId = resourceId;
  }

  public String getResourceType() {
    return resourceType;
  }

  public Object getResourceId() {
    return resourceId;
  }
}
