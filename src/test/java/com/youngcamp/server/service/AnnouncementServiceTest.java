package com.youngcamp.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.domain.AnnouncementContents;
import com.youngcamp.server.dto.AnnouncementRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementDeleteRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementPostRequest;
import com.youngcamp.server.dto.AnnouncementResponse.AnnouncementPostResponse;
import com.youngcamp.server.exception.NotFoundException;
import com.youngcamp.server.repository.AnnouncementRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnnouncementServiceTest {

  @Mock private AnnouncementRepository announcementRepository;

  @InjectMocks private AnnouncementService target;

  private final String imageUrl = "s3-image-url";
  private final Boolean isPinned = true;

  @Test
  public void 공지사항등록성공() {
    // given
    doReturn(announcement()).when(announcementRepository).save(any(Announcement.class));
    AnnouncementPostRequest request =
        AnnouncementPostRequest.builder()
            .imageUrl(imageUrl)
            .isPinned(isPinned)
            .contents(
                Arrays.asList(
                    AnnouncementRequest.AnnouncementTrRequest.builder()
                        .languageCode("ko")
                        .title("Ktitle")
                        .content("Kcontent")
                        .build(),
                    AnnouncementRequest.AnnouncementTrRequest.builder()
                        .languageCode("en")
                        .title("Etitle")
                        .content("Econtent")
                        .build()))
            .build();

    // when
    final AnnouncementPostResponse result = target.addAnnouncement(request);

    // then
    assertThat(result.getId()).isNotNull();

    // verify
    verify(announcementRepository, times(1)).save(any(Announcement.class));
  }

  @Test
  public void 공지사항삭제실패_존재하지않은글삭제시도() {
    // given
    doReturn(Collections.emptyList())
        .when(announcementRepository)
        .findExistingIds(Arrays.asList(-1L, -2L));
    AnnouncementDeleteRequest request =
        AnnouncementDeleteRequest.builder().ids(Arrays.asList(-1L, -2L)).build();

    // when
    final NotFoundException result =
        assertThrows(NotFoundException.class, () -> target.deleteAnnouncement(request));

    // then
    assertThat(result.getResourceType()).isEqualTo("Announcement");
  }

  @Test
  public void 공지사항상세조회실패_존재하지않은글조회시도() {
    // given
    Long announcementId = 1L;
    doReturn(Optional.empty()).when(announcementRepository).findByIdWithContents(any(Long.class));

    // when
    NotFoundException result =
        assertThrows(
            NotFoundException.class, () -> target.getDetailAnnouncement(announcementId, "ko"));

    // then
    assertThat(result.getResourceType()).isEqualTo("Announcement");
    assertThat(result.getResourceId()).isEqualTo(String.valueOf(announcementId));
  }

  private Announcement announcement() {
    Announcement announcement =
        Announcement.builder().id(-1L).imageUrl(imageUrl).isPinned(isPinned).build();

    AnnouncementContents translationKo =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("ko")
            .title("Ktitle")
            .content("Kcontent")
            .build();

    AnnouncementContents translationEn =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("en")
            .title("Etitle")
            .content("Econtent")
            .build();

    announcement.addContents(Arrays.asList(translationKo, translationEn));

    return announcement;
  }
}
