package com.youngcamp.server.domain;

import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementEditRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
  private String title;

  @Column(nullable = false, columnDefinition = "text")
  private String content;

  @Column(nullable = false)
  private Boolean isPinned;

  private String imageUrl;

  private String fileUrl;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public void editAnnouncement(AnnouncementEditRequest request) {
    this.title = request.getTitle();
    this.content = request.getContent();
    this.imageUrl = request.getImageUrl();
    this.fileUrl = request.getFileUrl();
    this.isPinned = request.getIsPinned();
    this.updatedAt = LocalDateTime.now();
  }
}
