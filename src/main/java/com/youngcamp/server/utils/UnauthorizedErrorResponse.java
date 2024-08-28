package com.youngcamp.server.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Unauthorized Error Response")
public class UnauthorizedErrorResponse extends ErrorResponse {
  public UnauthorizedErrorResponse(String message, List<ErrorDetail> errors) {
    super(message, errors);
  }
}
