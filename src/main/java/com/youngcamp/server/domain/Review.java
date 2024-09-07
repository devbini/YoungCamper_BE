package com.youngcamp.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youngcamp.server.service.ReviewService;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "review",
    indexes = {@Index(name = "idx_sequence", columnList = "sequence")})
@Getter
@Setter
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private Integer sequence;

  @Column(nullable = false, length = 500)
  private String content;

  @Column(nullable = false)
  @JsonIgnore
  private String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "review_id"))
  @Column(name = "image_url")
  private List<String> imageUrls = new ArrayList<>();

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  private void prePersist() {
    if (this.sequence == null) {
      this.sequence = generateNextSequence();
    }
    ZonedDateTime nowInKorea = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    this.createdAt = nowInKorea.toLocalDateTime();
    this.updatedAt = nowInKorea.toLocalDateTime();
  }

  @PreUpdate
  private void preUpdate() {
    ZonedDateTime nowInKorea = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    this.updatedAt = nowInKorea.toLocalDateTime();
  }

  private Integer generateNextSequence() {
    return ReviewService.getNextSequenceValue();
  }
}
