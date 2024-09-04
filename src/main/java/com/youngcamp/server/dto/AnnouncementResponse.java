package com.youngcamp.server.dto;

import com.youngcamp.server.domain.Announcement;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class AnnouncementResponse {

  @Builder
  @Getter
  public static class AnnouncementPostResponse {
    private Long id;
  }

  @Builder
  @Getter
  public static class AnnouncementEditResponse {
    private Long id;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AnnouncementGetResponse {
    private Long id;
    private String title;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public AnnouncementGetResponse(Announcement announcement) {
      this.id = announcement.getId();
      this.title = announcement.getTitle();
      this.isPinned = announcement.getIsPinned();
    }
  }

  @Builder
  @Getter
  public static class AnnouncementGetDetailResponse {
    private String title;
    private String content;
    private String imageUrl;
    private String fileUrl;
    private Boolean isPinned;
    private String createdAt;
    private String updatedAt;
  }
}
