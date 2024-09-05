package com.youngcamp.server.domain;

import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementEditRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Announcement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Boolean isPinned;

  private String imageUrl;

  private String fileUrl;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @OneToMany(
      mappedBy = "announcement",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  @Builder.Default
  private List<AnnouncementContents> contents = new ArrayList<>();;

  public void addContents(List<AnnouncementContents> contents) {
    this.contents.addAll(contents);
  }

  public void editAnnouncement(AnnouncementEditRequest request) {
    this.isPinned = request.getIsPinned();
    this.imageUrl = request.getImageUrl();
    this.fileUrl = request.getFileUrl();
    this.updatedAt = LocalDateTime.now();
  }
}
