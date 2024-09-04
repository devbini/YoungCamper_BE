package com.youngcamp.server.dto;

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
  @Builder
  public static class AnnouncementGetResponse {
    private Long id;
    private String title;
    private Boolean isPinned;
    private String createdAt;
    private String updatedAt;
  }

  @Builder
  @Getter
  public static class AnnouncementGetDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String fileUrl;
    private Boolean isPinned;
    private String createdAt;
    private String updatedAt;
  }
}
