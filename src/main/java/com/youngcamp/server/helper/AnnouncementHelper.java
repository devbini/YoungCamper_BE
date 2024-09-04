package com.youngcamp.server.helper;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.dto.AnnouncementResponse;

import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementHelper {

  public static List<AnnouncementResponse.AnnouncementGetResponse> toDto(
          List<Announcement> announcement) {
    List<AnnouncementResponse.AnnouncementGetResponse> dto = announcement.stream()
            .map(a ->
                    AnnouncementResponse.AnnouncementGetResponse.builder()
                            .id(a.getId())
                            .title(a.getTitle())
                            .isPinned(a.getIsPinned())
                            .createdAt(String.valueOf(a.getCreatedAt()))
                            .updatedAt(String.valueOf(a.getUpdatedAt()))
                            .build()
            )
            .collect(Collectors.toList());
    List<AnnouncementResponse.AnnouncementGetResponse> collect = dto;

    return dto;
  }

  public static AnnouncementResponse.AnnouncementGetDetailResponse toDto(
      Announcement announcement) {
    AnnouncementResponse.AnnouncementGetDetailResponse dto =
        AnnouncementResponse.AnnouncementGetDetailResponse.builder()
            .id(announcement.getId())
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
