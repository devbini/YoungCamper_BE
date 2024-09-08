package com.youngcamp.server.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementDetailProjection {
  private Long announcementId;
  private Boolean isPinned;
  private String imageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String languageCode;
  private String title;
  private String content;

  public AnnouncementDetailProjection(
      Long announcementId,
      Boolean isPinned,
      String imageUrl,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String languageCode,
      String title,
      String content) {
    this.announcementId = announcementId;
    this.isPinned = isPinned;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.languageCode = languageCode;
    this.title = title;
    this.content = content;
  }
}
