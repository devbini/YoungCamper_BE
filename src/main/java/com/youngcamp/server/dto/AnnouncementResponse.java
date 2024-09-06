package com.youngcamp.server.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class AnnouncementResponse {

  @Builder
  @Getter
  @Setter
  public static class AnnouncementPostResponse {
    private Long id;
    private String imageUrl;
  }

  @Builder
  @Getter
  @Setter
  public static class AnnouncementEditResponse {
    private Long id;
    private String imageUrl;
    private Boolean isPinned;
    private List<AnnouncementTrResponse> contents;
  }

  // 목록 조회용 DTO - 단일 언어 콘텐츠 포함
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class AnnouncementGetResponse {
    private Long id;
    private Boolean isPinned;
    private String createdAt;
    private String updatedAt;
    private AnnouncementTrResponse content;
  }

  // 상세 조회용 DTO - 단일 언어 콘텐츠 포함
  @Builder
  @Getter
  public static class AnnouncementGetDetailResponse {
    private Long id;
    private String imageUrl;
    private String fileUrl;
    private Boolean isPinned;
    private String createdAt;
    private String updatedAt;
    private String languageCode;
    private String title;
    private String content;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AnnouncementTrResponse {
    private String languageCode;
    private String title;
    private String content;
  }
}
