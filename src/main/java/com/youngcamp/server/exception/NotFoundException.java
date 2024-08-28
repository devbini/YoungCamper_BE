package com.youngcamp.server.exception;

public class NotFoundException extends RuntimeException {

  private final String resourceType;
  private final String resourceId;

  // 생성자: 자원 타입과 ID를 추가로 받음
  public NotFoundException(String resourceType, String resourceId, String message) {
    super(message);
    this.resourceType = resourceType;
    this.resourceId = resourceId;
  }

  public String getResourceType() {
    return resourceType;
  }

  public String getResourceId() {
    return resourceId;
  }
}
