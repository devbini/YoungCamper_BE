package com.youngcamp.server.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Bad Request Error Response")
public class BadRequestErrorResponse extends ErrorResponse {
  public BadRequestErrorResponse(String message, List<ErrorDetail> errors) {
    super(message, errors);
  }
}
