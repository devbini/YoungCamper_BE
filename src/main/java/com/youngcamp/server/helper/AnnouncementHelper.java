package com.youngcamp.server.helper;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.dto.AnnouncementResponse;

public class AnnouncementHelper {

  public static AnnouncementResponse.AnnouncementGetDetailResponse toDto(
      Announcement announcement) {
    AnnouncementResponse.AnnouncementGetDetailResponse dto =
        AnnouncementResponse.AnnouncementGetDetailResponse.builder()
            .title(announcement.getTitle())
            .content(announcement.getContent())
            .imageUrl(announcement.getImageUrl())
            .fileUrl(announcement.getFileUrl())
            .isPinned(announcement.getIsPinned())
            .createdAt(String.valueOf(announcement.getCreatedAt()))
            .updatedAt(String.valueOf(announcement.getUpdatedAt()))
            .build();

    return dto;
  }
}
