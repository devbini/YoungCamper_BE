package com.youngcamp.server.controller;

import com.youngcamp.server.domain.Review;
import com.youngcamp.server.dto.ReviewDTO;
import com.youngcamp.server.exception.NotFoundException;
import com.youngcamp.server.service.ReviewService;
import com.youngcamp.server.utils.SuccessResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

  private final ReviewService reviewService;
  private final View error;

  public ReviewController(ReviewService reviewService, View error) {
    this.reviewService = reviewService;
    this.error = error;
  }

  @GetMapping
  public SuccessResponse<Page<Review>> getAllReviews(Pageable pageable) {
    Page<Review> reviews = reviewService.getAllReviews(pageable);
    return new SuccessResponse<>("리뷰 조회 성공", reviews);
  }

  @GetMapping("/sequence/{sequence}")
  public SuccessResponse<Review> getReviewBySequence(@PathVariable Integer sequence) {
    Review review = reviewService.getReviewBySequence(sequence);
    if (review != null) {
      return new SuccessResponse<>("리뷰 조회 성공", review);
    } else {
      throw new NotFoundException("review", sequence, "sequence에 해당하는 리뷰를 찾을 수 없습니다.");
    }
  }

  @PostMapping
  public SuccessResponse<Review> createReview(@RequestBody ReviewDTO.PostReviewRequest review) {

    Review savedReview = reviewService.createReview(review);
    return new SuccessResponse<>("리뷰 등록 성공", savedReview);
  }

  @PutMapping("/{id}")
  public SuccessResponse<Review> updateReview(
      @PathVariable UUID id, @RequestBody ReviewDTO.UpdateReviewRequest review) {
    Review reviewDetails = new Review();
    reviewDetails.setContent(review.getContent());
    reviewDetails.setImageUrls(review.getImageUrls());

    Review updatedReview = reviewService.updateReview(id, reviewDetails, review.getPassword());
    if (updatedReview != null) {
      return new SuccessResponse<>("리뷰 업데이트 성공", updatedReview);
    } else {
      throw new NotFoundException("review", id, "id에 해당하는 리뷰를 찾을 수 없습니다.");
    }
  }

  @DeleteMapping("/{id}")
  public SuccessResponse<Void> deleteReview(
      @PathVariable UUID id, @RequestBody ReviewDTO.DeleteReviewRequest request) {
    reviewService.deleteReview(id, request.getPassword());
    return new SuccessResponse<>("리뷰 삭제 성공", null);
  }
}
