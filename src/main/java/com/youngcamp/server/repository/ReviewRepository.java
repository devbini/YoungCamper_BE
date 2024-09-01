package com.youngcamp.server.repository;

import com.youngcamp.server.domain.Review;
import io.micrometer.common.lang.NonNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  List<Review> findByContent(String content);

  Optional<Review> findBySequence(Integer sequence);

  Page<Review> findAll(@NonNull Pageable pageable);

  Review findTopByOrderBySequenceDesc();
}
