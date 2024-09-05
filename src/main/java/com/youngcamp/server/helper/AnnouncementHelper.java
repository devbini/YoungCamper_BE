package com.youngcamp.server.helper;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.domain.AnnouncementContents;
import com.youngcamp.server.dto.AnnouncementResponse;
import java.util.List;
import java.util.stream.Collectors;

public class AnnouncementHelper {

  // 공지사항 목록을 DTO로 변환 (단일 언어 콘텐츠 사용)
  public static List<AnnouncementResponse.AnnouncementGetResponse> toDto(
      List<Announcement> announcements) {
    return announcements.stream()
        .map(
            a -> {
              AnnouncementContents filteredContent = a.getFilteredContent();

              // 필터링된 콘텐츠가 null일 경우 기본값을 설정
              AnnouncementResponse.AnnouncementTrResponse contentResponse =
                  (filteredContent != null)
                      ? AnnouncementResponse.AnnouncementTrResponse.builder()
                          .languageCode(filteredContent.getLanguageCode())
                          .title(filteredContent.getTitle())
                          .content(filteredContent.getContent())
                          .build()
                      : AnnouncementResponse.AnnouncementTrResponse.builder()
                          .languageCode("unknown")
                          .title("No title available")
                          .content("No content available")
                          .build();

              return AnnouncementResponse.AnnouncementGetResponse.builder()
                  .id(a.getId())
                  .isPinned(a.getIsPinned())
                  .createdAt(String.valueOf(a.getCreatedAt()))
                  .updatedAt(String.valueOf(a.getUpdatedAt()))
                  .content(contentResponse)
                  .build();
            })
        .collect(Collectors.toList());
  }

  // 공지사항 상세 내용을 DTO로 변환 (단일 언어 콘텐츠 사용)
  public static AnnouncementResponse.AnnouncementGetDetailResponse toDto(
      Announcement announcement) {
    AnnouncementContents filteredContent = announcement.getFilteredContent();

    // 필터링된 콘텐츠가 null일 경우 기본값을 설정
    AnnouncementResponse.AnnouncementTrResponse contentResponse =
        (filteredContent != null)
            ? AnnouncementResponse.AnnouncementTrResponse.builder()
                .languageCode(filteredContent.getLanguageCode())
                .title(filteredContent.getTitle())
                .content(filteredContent.getContent())
                .build()
            : AnnouncementResponse.AnnouncementTrResponse.builder()
                .languageCode("unknown")
                .title("No title available")
                .content("No content available")
                .build();

    return AnnouncementResponse.AnnouncementGetDetailResponse.builder()
        .id(announcement.getId())
        .imageUrl(announcement.getImageUrl())
        .fileUrl(announcement.getFileUrl())
        .isPinned(announcement.getIsPinned())
        .createdAt(String.valueOf(announcement.getCreatedAt()))
        .updatedAt(String.valueOf(announcement.getUpdatedAt()))
        .content(contentResponse)
        .build();
  }
}
