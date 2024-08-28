package com.youngcamp.server.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Not Found Error Response")
public class NotFoundErrorResponse extends ErrorResponse {
  public NotFoundErrorResponse(String message, List<ErrorDetail> errors) {
    super(message, errors);
  }
}
