package com.youngcamp.server.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementProjection {

  private Long id;
  private LocalDateTime createdAt;
  private Boolean isPinned;
  private String languageCode;
  private String title;

  public AnnouncementProjection(
      Long id, LocalDateTime createdAt, Boolean isPinned, String languageCode, String title) {
    this.id = id;
    this.createdAt = createdAt;
    this.isPinned = isPinned;
    this.languageCode = languageCode;
    this.title = title;
  }
}
