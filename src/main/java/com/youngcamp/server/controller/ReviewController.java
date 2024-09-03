package com.youngcamp.server.controller;

import com.youngcamp.server.annotation.AdminOnly;
import com.youngcamp.server.domain.Review;
import com.youngcamp.server.dto.ReviewDTO;
import com.youngcamp.server.dto.ReviewDTO.DeleteReviewRequest;
import com.youngcamp.server.exception.NotFoundException;
import com.youngcamp.server.helper.ReviewHelper;
import com.youngcamp.server.service.AdminChecker;
import com.youngcamp.server.service.ReviewService;
import com.youngcamp.server.utils.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

  private final ReviewService reviewService;
  private final AdminChecker adminChecker;

  public ReviewController(ReviewService reviewService, AdminChecker adminChecker) {
    this.reviewService = reviewService;
    this.adminChecker = adminChecker;
  }

  @GetMapping
  @Operation(
      summary = "리뷰 조회",
      description = "페이지네이션을 지원하는 모든 리뷰를 조회합니다.",
      parameters = {
        @Parameter(name = "page", description = "페이지 번호", example = "0"),
        @Parameter(name = "size", description = "페이지 크기", example = "5"),
        @Parameter(name = "sort", description = "정렬 기준 (예: createdAt)", example = "createdAt,desc")
      })
  public SuccessResponse<Page<ReviewDTO.Review>> getAllReviews(
      @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    Page<Review> reviews = reviewService.getAllReviews(pageable);
    Page<ReviewDTO.Review> reviewsDTO = reviews.map(ReviewHelper::toDto);
    return new SuccessResponse<>("리뷰 조회 성공", reviewsDTO);
  }

  @GetMapping("/{sequence}")
  @Operation(summary = "리뷰 상세 조회", description = "특정 시퀀스에 해당하는 리뷰를 조회합니다.")
  public SuccessResponse<Review> getReviewBySequence(
      @Parameter(description = "리뷰의 시퀀스 번호") @PathVariable Integer sequence) {
    Review review = reviewService.getReviewBySequence(sequence);
    if (review != null) {
      return new SuccessResponse<>("리뷰 조회 성공", review);
    } else {
      throw new NotFoundException("review", sequence, "sequence에 해당하는 리뷰를 찾을 수 없습니다.");
    }
  }

  @PostMapping
  @Operation(summary = "리뷰 등록", description = "새 리뷰를 등록합니다.")
  public SuccessResponse<ReviewDTO.Review> createReview(
      @RequestBody @Valid @Parameter(description = "등록할 리뷰의 정보")
          ReviewDTO.PostReviewRequest review) {
    Review savedReview = reviewService.createReview(review);
    ReviewDTO.Review reviewDTO = ReviewHelper.toDto(savedReview);
    return new SuccessResponse<>("리뷰 등록 성공", reviewDTO);
  }

  @PutMapping("/{id}")
  @Operation(summary = "리뷰 수정", description = "특정 ID의 리뷰를 수정합니다.")
  public SuccessResponse<ReviewDTO.Review> updateReview(
      @Parameter(description = "수정할 리뷰의 ID") @PathVariable UUID id,
      @RequestBody @Valid @Parameter(description = "수정할 리뷰의 정보")
          ReviewDTO.UpdateReviewRequest review) {
    Review reviewDetails = new Review();
    reviewDetails.setPassword(review.getPassword());
    reviewDetails.setContent(review.getContent());
    reviewDetails.setImageUrls(review.getImageUrls());

    Review updatedReview = reviewService.updateReview(id, reviewDetails);
    ReviewDTO.Review reviewDTO = ReviewHelper.toDto(updatedReview);
    if (updatedReview != null) {
      return new SuccessResponse<>("리뷰 업데이트 성공", reviewDTO);
    } else {
      throw new NotFoundException("review", id, "id에 해당하는 리뷰를 찾을 수 없습니다.");
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "리뷰 삭제", description = "특정 ID의 리뷰를 삭제합니다.")
  // TODO: admin 권한의 경우 비밀번호 대조 없이 삭제 가능하도록 로직 추가
  public SuccessResponse<Void> deleteReview(
      @PathVariable UUID id, @RequestBody DeleteReviewRequest request) {
    reviewService.deleteReview(id, request.getPassword());
    return new SuccessResponse<>("리뷰 삭제 성공", null);
  }

  @DeleteMapping
  @Operation(summary = "다수 리뷰 삭제", description = "다수의 리뷰를 삭제합니다. 관리자 권한이 필요합니다.")
  @AdminOnly
  public SuccessResponse<Void> deleteManyReviews(@RequestBody List<UUID> ids) {
    reviewService.deleteManyReviews(ids);
    return new SuccessResponse<>("리뷰 다수 삭제 성공", null);
  }
}
