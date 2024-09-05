package com.youngcamp.server.helper;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.domain.AnnouncementContents;
import com.youngcamp.server.dto.AnnouncementResponse;
import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementHelper {

  public static List<AnnouncementResponse.AnnouncementGetResponse> toDto(
      List<Announcement> announcements) {
    List<AnnouncementResponse.AnnouncementGetResponse> dto =
        announcements.stream()
            .map(
                a -> {
                  String translatedTitle =
                      a.getContents().stream()
                          .map(AnnouncementContents::getTitle)
                          .findFirst()
                          .orElse("정보없음");

                  return AnnouncementResponse.AnnouncementGetResponse.builder()
                      .id(a.getId())
                      .isPinned(a.getIsPinned())
                      .createdAt(String.valueOf(a.getCreatedAt()))
                      .updatedAt(String.valueOf(a.getUpdatedAt()))
                      .contents(
                          a.getContents().stream()
                              .map(
                                  t ->
                                      AnnouncementResponse.AnnouncementTrResponse.builder()
                                          .languageCode(t.getLanguageCode())
                                          .title(t.getTitle())
                                          .content(t.getContent())
                                          .build())
                              .collect(Collectors.toList()))
                      .build();
                })
            .collect(Collectors.toList());

    return dto;
  }

  public static AnnouncementResponse.AnnouncementGetDetailResponse toDto(
      Announcement announcement) {
    return AnnouncementResponse.AnnouncementGetDetailResponse.builder()
        .id(announcement.getId())
        .imageUrl(announcement.getImageUrl())
        .fileUrl(announcement.getFileUrl())
        .isPinned(announcement.getIsPinned())
        .createdAt(String.valueOf(announcement.getCreatedAt()))
        .updatedAt(String.valueOf(announcement.getUpdatedAt()))
        .contents(
            announcement.getContents().stream()
                .map(
                    t ->
                        AnnouncementResponse.AnnouncementTrResponse.builder()
                            .languageCode(t.getLanguageCode())
                            .title(t.getTitle())
                            .content(t.getContent())
                            .build())
                .collect(Collectors.toList()))
        .build();
  }
}
