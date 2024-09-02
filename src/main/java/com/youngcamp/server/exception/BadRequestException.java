package com.youngcamp.server.exception;

import com.youngcamp.server.utils.ErrorDetail;
import java.util.List;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

  private final List<ErrorDetail> errors;

  public BadRequestException(String message, List<ErrorDetail> errors) {
    super(message);
    this.errors = errors;
  }
}
