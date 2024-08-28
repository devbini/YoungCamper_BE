package com.youngcamp.server.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Forbidden Error Response")
public class ForbiddenErrorResponse extends ErrorResponse {
  public ForbiddenErrorResponse(String message, List<ErrorDetail> errors) {
    super(message, errors);
  }
}
