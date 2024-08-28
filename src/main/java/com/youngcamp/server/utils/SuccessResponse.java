package com.youngcamp.server.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "성공 응답 구조")
public class SuccessResponse<T> {

  @Schema(description = "응답 상태", example = "success")
  private String status;

  @Schema(description = "응답 메시지", example = "Request processed successfully")
  private String message;

  @Schema(description = "응답 데이터")
  private T data;

  public SuccessResponse(String message, T data) {
    this.status = "success";
    this.message = message;
    this.data = data;
  }

  // Getter와 Setter
  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public T getData() {
    return data;
  }
}
