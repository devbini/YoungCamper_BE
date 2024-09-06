package com.youngcamp.server.service;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.domain.AnnouncementContents;
import com.youngcamp.server.dto.AnnouncementDetailProjection;
import com.youngcamp.server.dto.AnnouncementProjection;
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
    announcementRepository.deleteAllById(request.getIds());
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
  public List<AnnouncementProjection> getAnnouncements(String languageCode) {
    return announcementRepository.findAllByLanguageCodeOrderByCreatedAtDesc(languageCode);
  }

  @Transactional
  public AnnouncementDetailProjection getDetailAnnouncement(
      Long announcementId, String languageCode) {
    AnnouncementDetailProjection announcementDetail =
        announcementRepository
            .findAnnouncementDetailByIdAndLanguageCode(announcementId, languageCode)
            .orElseThrow(() -> new NotFoundException("공지사항", announcementId, "찾을 수 없는 공지사항 입니다."));
    return announcementDetail;
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
  public List<AnnouncementProjection> searchAnnouncements(String keyword, String languageCode) {
    return announcementRepository.findAllByKeywordAndLanguageCode(keyword, languageCode);
  }
}
