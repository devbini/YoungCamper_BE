package com.youngcamp.server.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ImageUrlPrefixValidator implements ConstraintValidator<ImageUrlPrefix, List<String>> {

  private static final String REQUIRED_PREFIX = "https://dpy17a4qtt448.cloudfront.net/";

  @Override
  public void initialize(ImageUrlPrefix constraintAnnotation) {}

  @Override
  public boolean isValid(List<String> imageUrls, ConstraintValidatorContext context) {
    if (imageUrls == null) {
      return true;
    }
    boolean isValid = imageUrls.stream().allMatch(url -> url.startsWith(REQUIRED_PREFIX));
    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Image URL must start with " + REQUIRED_PREFIX)
          .addConstraintViolation();
    }
    return isValid;
  }
}
