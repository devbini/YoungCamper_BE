package com.youngcamp.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AnnouncementRequest {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AnnouncementPostRequest {
    private String imageUrl;
    private String fileUrl;
    private Boolean isPinned;
    private List<AnnouncementTrRequest> contents;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AnnouncementDeleteRequest {
    private List<Long> ids;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class AnnouncementEditRequest {
    private String imageUrl;
    private String fileUrl;
    private Boolean isPinned;
    private List<AnnouncementTrRequest> contents;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AnnouncementTrRequest {
    private String languageCode;
    private String title;
    private String content;
  }
}
