package com.youngcamp.server.service;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.domain.AnnouncementContents;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementDeleteRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementEditRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementPostRequest;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementEditResponse;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementPostResponse;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementTrResponse;
import com.youngcamp.server.exception.NotFoundException;
import com.youngcamp.server.repository.AnnouncementRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AnnouncementService {

  private final AnnouncementRepository announcementRepository;

  @Transactional
  public AnnouncementPostResponse addAnnouncement(AnnouncementPostRequest request) {

    Announcement announcement =
        Announcement.builder()
            .imageUrl(request.getImageUrl())
            .fileUrl(request.getFileUrl())
            .isPinned(request.getIsPinned())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    List<AnnouncementContents> contents =
        request.getContents().stream()
            .map(
                trRequest ->
                    AnnouncementContents.builder()
                        .announcement(announcement)
                        .languageCode(trRequest.getLanguageCode())
                        .title(trRequest.getTitle())
                        .content(trRequest.getContent())
                        .build())
            .collect(Collectors.toList());

    announcement.addContents(contents); // contents에 null 체크가 포함됨
    Announcement savedAnnouncement = announcementRepository.save(announcement);

    return AnnouncementPostResponse.builder().id(savedAnnouncement.getId()).build();
  }

  @Transactional
  public void deleteAnnouncement(AnnouncementDeleteRequest request) {
    existIdValidation(request.getIds());
    announcementRepository.deleteAllByIds(request.getIds());
  }

  private void existIdValidation(List<Long> ids) {
    List<Long> existingIds = announcementRepository.findExistingIds(ids);
    List<Long> nonExistingIds =
        ids.stream().filter(id -> !existingIds.contains(id)).collect(Collectors.toList());

    if (!nonExistingIds.isEmpty()) {
      log.error("존재하지 않는 ID: {}", nonExistingIds);
      throw new NotFoundException("Announcement", null, "존재하지 않는 ID가 포함되어 있습니다");
    }
  }

  @Transactional
  public List<Announcement> getAnnouncements(String languageCode) {
    // 공지사항을 불러오고, 언어 코드에 따라 필터링
    List<Announcement> announcements = announcementRepository.findAllByOrderByCreatedAtDesc();
    announcements.forEach(announcement -> filterContentByLanguage(announcement, languageCode));
    System.out.println(announcements);
    return announcements;
  }

  @Transactional
  public Announcement getDetailAnnouncement(Long announcementId, String languageCode) {
    Announcement announcement =
        announcementRepository
            .findByIdWithContents(announcementId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Announcement",
                        String.valueOf(announcementId),
                        "Resource with the specified ID was not found"));
    filterContentByLanguage(announcement, languageCode);
    return announcement;
  }

  private void filterContentByLanguage(Announcement announcement, String languageCode) {
    // 언어에 따른 콘텐츠 필터링, 기본값은 한국어로 설정
    AnnouncementContents translatedContent =
        announcement.getContents().stream()
            .filter(content -> content.getLanguageCode().equals(languageCode))
            .findFirst()
            .orElseGet(() -> getDefaultLanguageContent(announcement)); // 기본값 한국어

    announcement.setFilteredContent(translatedContent); // 필터링된 콘텐츠 저장
  }

  private AnnouncementContents getDefaultLanguageContent(Announcement announcement) {
    // 한국어 콘텐츠가 없으면 첫 번째로 등록된 콘텐츠 반환
    return announcement.getContents().stream()
        .filter(content -> content.getLanguageCode().equals("ko"))
        .findFirst()
        .orElse(announcement.getContents().get(0)); // 첫 번째 콘텐츠 반환
  }

  @Transactional
  public AnnouncementEditResponse editAnnouncement(
      Long announcementId, AnnouncementEditRequest request) {
    // 공지사항이 존재하는지 확인
    Announcement announcement =
        announcementRepository
            .findById(announcementId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Announcement", String.valueOf(announcementId), "공지사항을 찾을 수 없습니다."));

    // 이미지 URL과 파일 URL을 업데이트
    if (request.getImageUrl() != null) {
      announcement.setImageUrl(request.getImageUrl());
    }
    if (request.getFileUrl() != null) {
      announcement.setFileUrl(request.getFileUrl());
    }

    // 고정 여부 업데이트
    if (request.getIsPinned() != null) {
      announcement.setIsPinned(request.getIsPinned());
    }

    // 기존의 콘텐츠를 모두 지우고 새롭게 추가
    if (request.getContents() != null && !request.getContents().isEmpty()) {
      // 기존의 콘텐츠 지우기
      announcement.getContents().clear();

      // 새로운 콘텐츠 추가
      List<AnnouncementContents> updatedContents =
          request.getContents().stream()
              .map(
                  trRequest ->
                      AnnouncementContents.builder()
                          .announcement(announcement)
                          .languageCode(trRequest.getLanguageCode())
                          .title(trRequest.getTitle())
                          .content(trRequest.getContent())
                          .build())
              .collect(Collectors.toList());

      announcement.addContents(updatedContents);
    }

    // 수정된 공지사항 저장
    Announcement updatedAnnouncement = announcementRepository.save(announcement);

    // 수정된 공지사항을 DTO로 변환 후 반환
    List<AnnouncementTrResponse> contentResponses =
        updatedAnnouncement.getContents().stream()
            .map(
                content ->
                    AnnouncementTrResponse.builder()
                        .languageCode(content.getLanguageCode())
                        .title(content.getTitle())
                        .content(content.getContent())
                        .build())
            .collect(Collectors.toList());

    return AnnouncementEditResponse.builder()
        .id(updatedAnnouncement.getId())
        .imageUrl(updatedAnnouncement.getImageUrl())
        .isPinned(updatedAnnouncement.getIsPinned())
        .contents(contentResponses) // contents 필드 추가
        .build();
  }

  @Transactional
  public List<Announcement> searchAnnouncements(String keyword, String languageCode) {
    return announcementRepository.findByKeywordAndLanguageCode(keyword, languageCode);
  }
}
