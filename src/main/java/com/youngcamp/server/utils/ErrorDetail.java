package com.youngcamp.server.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "개별 에러 정보")
public class ErrorDetail {

  private String code;

  private String message;

  private String details;

  // 기본 생성자
  public ErrorDetail() {}

  // 전체 필드를 사용하는 생성자
  public ErrorDetail(String code, String message) {
    this.code = code;
    this.message = message;
    this.details = null; // 기본값은 null
  }

  public ErrorDetail(String code, String message, String details) {
    this.code = code;
    this.message = message;
    this.details = details;
  }

  // Getter와 Setter 메소드
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}
