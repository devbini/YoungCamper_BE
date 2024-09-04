package com.youngcamp.server.service;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementDeleteRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementEditRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementPostRequest;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementEditResponse;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementGetDetailResponse;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementGetResponse;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementPostResponse;
import com.youngcamp.server.exception.NotFoundException;
import com.youngcamp.server.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    @Transactional
    public AnnouncementPostResponse addAnnouncement(AnnouncementPostRequest request) {

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .fileUrl(request.getFileUrl())
                .isPinned(request.getIsPinned())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        return AnnouncementPostResponse.builder()
                .id(savedAnnouncement.getId())
                .build();
    }

    @Transactional
    public void deleteAnnouncement(AnnouncementDeleteRequest request) {

        existIdValidation(request.getIds());
        announcementRepository.deleteAllAnnouncementById(request.getIds());
    }

    private void existIdValidation(List<Long> ids) {
        List<Long> existingIds = announcementRepository.findExistingIds(ids);

        List<Long> nonExistingIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());

        if (!nonExistingIds.isEmpty()) {
            throw new NotFoundException("Announcement", null, "존재하지 않는 ID가 포함되어 있습니다");
        }
    }

    public List<AnnouncementGetResponse> getAnnouncements() {
        return announcementRepository.findAllOrderByCreatedAtDesc().stream()
                .map(AnnouncementGetResponse::new)
                .collect(Collectors.toList());
    }

    public AnnouncementGetDetailResponse getDetailAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NotFoundException("Announcement", String.valueOf(announcementId), "Resource with the specified ID was not found"));

        return AnnouncementGetDetailResponse.builder()
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .imageUrl(announcement.getImageUrl())
                .fileUrl(announcement.getFileUrl())
                .isPinned(announcement.getIsPinned())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }

    public AnnouncementEditResponse editAnnouncement(Long announcementId, AnnouncementEditRequest request) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NotFoundException("Announcement", String.valueOf(announcementId), "Resource with the specified ID was not found"));

        announcement.editAnnouncement(request);

        return AnnouncementEditResponse.builder()
                .id(announcement.getId())
                .build();
    }

    public List<AnnouncementGetResponse> searchAnnouncements(String keyword) {
        List<Announcement> foundAnnouncements = announcementRepository.findByTitleLikeOrderByCreatedAtDesc(keyword);

        return foundAnnouncements.stream()
                .map(AnnouncementGetResponse::new)
                .collect(Collectors.toList());
    }
}
