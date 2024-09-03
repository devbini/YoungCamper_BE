package com.youngcamp.server.service;

import com.youngcamp.server.domain.Review;
import com.youngcamp.server.dto.ReviewDTO;
import com.youngcamp.server.exception.NotFoundException;
import com.youngcamp.server.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
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

  public Review updateReview(UUID id, Review reviewDetails) {
    String password = reviewDetails.getPassword();
    return reviewRepository
        .findById(id)
        .map(
            review -> {
              if (passwordEncoder.matches(password, review.getPassword())) {
                review.setContent(reviewDetails.getContent());
                if (!password.isEmpty()) {
                  review.setPassword(passwordEncoder.encode(reviewDetails.getPassword()));
                }
                review.setImageUrls(reviewDetails.getImageUrls());
                review.setUpdatedAt(LocalDateTime.now());
                return reviewRepository.save(review);
              } else {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
              }
            })
        .orElseThrow(() -> new NotFoundException("id", id, "존재하지 않는 리뷰입니다."));
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
              throw new NotFoundException("review", id, "존재하지 않는 리뷰입니다.");
            });
  }

  @Transactional
  public void deleteManyReviews(List<UUID> ids) {
    List<Review> reviews = reviewRepository.findAllById(ids);
    if (reviews.isEmpty()) {
      throw new NotFoundException("reviews", ids, "존재하지 않는 리뷰가 포함되어 있습니다.");
    }
    reviewRepository.deleteAll(reviews);
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
