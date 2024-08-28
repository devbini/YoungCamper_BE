package com.youngcamp.server.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "에러 응답 구조")
public class ErrorResponse {

  @Schema(description = "응답 상태", example = "error")
  private String status;

  @Schema(description = "응답 메시지", example = "An error occurred")
  private String message;

  @Schema(description = "에러 정보 목록")
  private List<ErrorDetail> errors;

  public ErrorResponse(String message, List<ErrorDetail> errors) {
    this.status = "error";
    this.message = message;
    this.errors = errors;
  }

  // Getter와 Setter 메소드
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<ErrorDetail> getErrors() {
    return errors;
  }

  public void setErrors(List<ErrorDetail> errors) {
    this.errors = errors;
  }
}
