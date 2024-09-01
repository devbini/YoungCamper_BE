package com.youngcamp.server.helper;

import com.youngcamp.server.domain.Review;
import com.youngcamp.server.dto.ReviewDTO;

public class ReviewHelper {
  public static ReviewDTO.Review toDto(Review review) {
    ReviewDTO.Review dto = new ReviewDTO.Review();
    dto.setId(review.getId());
    dto.setSequence(review.getSequence());
    dto.setContent(review.getContent());
    dto.setImageUrls(review.getImageUrls());
    dto.setCreatedAt(String.valueOf(review.getCreatedAt()));
    dto.setUpdatedAt(String.valueOf(review.getUpdatedAt()));
    return dto;
  }
}
