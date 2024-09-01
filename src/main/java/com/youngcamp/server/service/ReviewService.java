package com.youngcamp.server.service;

import com.youngcamp.server.domain.Review;
import com.youngcamp.server.dto.ReviewDTO;
import com.youngcamp.server.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final PasswordEncoder passwordEncoder;
  private static ReviewService instance;

  public ReviewService(ReviewRepository reviewRepository, PasswordEncoder passwordEncoder) {
    this.reviewRepository = reviewRepository;
    this.passwordEncoder = passwordEncoder;
    instance = this;
  }

  public Page<Review> getAllReviews(Pageable pageable) {
    return reviewRepository.findAll(pageable);
  }

  public Review getReviewBySequence(Integer sequence) {
    return reviewRepository.findBySequence(sequence).orElse(null);
  }

  public Review createReview(ReviewDTO.PostReviewRequest request) {
    Review review = new Review();
    review.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 해싱
    review.setContent(request.getContent());
    review.setImageUrls(request.getImageUrls());
    review.setCreatedAt(LocalDateTime.now());
    review.setUpdatedAt(LocalDateTime.now());
    return reviewRepository.save(review);
  }

  public Review updateReview(UUID id, Review reviewDetails, String password) {
    return reviewRepository
        .findById(id)
        .map(
            review -> {
              if (passwordEncoder.matches(password, review.getPassword())) { // 비밀번호 대조
                review.setContent(reviewDetails.getContent());
                if (!reviewDetails.getPassword().isEmpty()) { // 비밀번호가 제공된 경우에만 업데이트
                  review.setPassword(passwordEncoder.encode(reviewDetails.getPassword()));
                }
                review.setImageUrls(reviewDetails.getImageUrls());
                review.setUpdatedAt(LocalDateTime.now());
                return reviewRepository.save(review);
              } else {
                throw new IllegalArgumentException("Incorrect password");
              }
            })
        .orElseThrow(() -> new IllegalArgumentException("Review not found"));
  }

  public void deleteReview(UUID id, String password) {
    reviewRepository
        .findById(id)
        .ifPresentOrElse(
            review -> {
              if (passwordEncoder.matches(password, review.getPassword())) {
                reviewRepository.deleteById(id);
              } else {
                throw new IllegalArgumentException("Incorrect password");
              }
            },
            () -> {
              throw new IllegalArgumentException("Review not found");
            });
  }

  public static Integer getNextSequenceValue() {
    return instance.generateNextSequence();
  }

  private Integer generateNextSequence() {
    Review reviewWithMaxSequence = reviewRepository.findTopByOrderBySequenceDesc();
    if (reviewWithMaxSequence != null) {
      return reviewWithMaxSequence.getSequence() + 1;
    } else {
      return 1;
    }
  }
}
