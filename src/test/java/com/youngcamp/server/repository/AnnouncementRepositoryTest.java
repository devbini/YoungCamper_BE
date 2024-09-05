package com.youngcamp.server.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.domain.AnnouncementContents;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AnnouncementRepositoryTest {

  @Autowired private AnnouncementRepository announcementRepository;

  @Test
  public void AnnouncementRepository_IS_NOTNULL_기본테스트() {
    assertThat(announcementRepository).isNotNull();
  }

  @BeforeEach
  void clean() {
    announcementRepository.deleteAll();
  }

  @Test
  public void 공지사항등록() {
    // given
    Announcement announcement =
        Announcement.builder().isPinned(true).imageUrl("s3-image-url").build();

    AnnouncementContents translationKo =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("ko")
            .title("KTitle")
            .content("KContent")
            .build();

    AnnouncementContents translationEn =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("en")
            .title("English Title")
            .content("English Content")
            .build();

    announcement.addContents(List.of(translationKo, translationEn));

    // when
    Announcement savedAnnouncement = announcementRepository.save(announcement);

    // then
    assertThat(savedAnnouncement.getContents()).hasSize(2);
    assertThat(savedAnnouncement.getContents().get(0).getTitle()).isEqualTo("KTitle");
    assertThat(savedAnnouncement.getContents().get(1).getTitle()).isEqualTo("English Title");
  }

  @Transactional
  @Test
  public void 공지사항삭제_1개() {
    // given
    Announcement announcement = Announcement.builder()
        .isPinned(true)
        .imageUrl("s3-image-url")
        .build();

    AnnouncementContents translationKo = AnnouncementContents.builder()
        .announcement(announcement)
        .languageCode("ko")
        .title("Korean Title")
        .content("Korean Content")
        .build();

    announcement.addContents(List.of(translationKo));
System.out.println(announcement.getContents());
    // when
    Announcement savedAnnouncement = announcementRepository.save(announcement);
    announcementRepository.deleteAllById(List.of(savedAnnouncement.getId()));

    Optional<Announcement> result = announcementRepository.findById(savedAnnouncement.getId());
System.out.println(result);
    // then
    assertThat(result).isEmpty();
  }


  @Test
  @Transactional
  public void 공지사항삭제_여러개() {
    // given
    List<Announcement> announcements =
        IntStream.range(0, 10)
            .mapToObj(
                i -> {
                  Announcement announcement =
                      Announcement.builder().imageUrl("s3-image-url#" + i).isPinned(true).build();
                  AnnouncementContents translationKo =
                      AnnouncementContents.builder()
                          .announcement(announcement)
                          .languageCode("ko")
                          .title("title" + i)
                          .content("content" + i)
                          .build();
                  announcement.addContents(List.of(translationKo));
                  return announcement;
                })
            .collect(Collectors.toList());

    List<Announcement> savedAnnouncements = announcementRepository.saveAll(announcements);

    List<Long> requestIds =
        savedAnnouncements.stream().map(Announcement::getId).collect(Collectors.toList());

    // when
    announcementRepository.deleteAllByIds(requestIds);
    List<Announcement> result = announcementRepository.findAllById(requestIds);

    // then
    assertThat(result).isEmpty();
  }


  @Test
  public void 존재하는공지사항만반환성공() {
    // given
    Announcement announcement1 =
        Announcement.builder().isPinned(false).imageUrl("image1.jpg").build();

    Announcement announcement2 =
        Announcement.builder().isPinned(false).imageUrl("image2.jpg").build();

    AnnouncementContents translationKo1 =
        AnnouncementContents.builder()
            .announcement(announcement1)
            .languageCode("ko")
            .title("공지사항 1")
            .content("내용 1")
            .build();

    AnnouncementContents translationKo2 =
        AnnouncementContents.builder()
            .announcement(announcement2)
            .languageCode("ko")
            .title("공지사항 2")
            .content("내용 2")
            .build();

    announcement1.addContents(List.of(translationKo1));
    announcement2.addContents(List.of(translationKo2));

    List<Announcement> savedAnnouncements =
        announcementRepository.saveAll(Arrays.asList(announcement1, announcement2));

    // when
    List<Long> idsToCheck =
        Arrays.asList(savedAnnouncements.get(0).getId(), savedAnnouncements.get(1).getId());

    List<Long> existingIds = announcementRepository.findExistingIds(idsToCheck);

    // then
    assertThat(existingIds)
        .containsExactlyInAnyOrder(
            savedAnnouncements.get(0).getId(), savedAnnouncements.get(1).getId());
    assertThat(existingIds.size()).isEqualTo(2);
  }

  @Test
  public void 공지사항조회성공() {
    // given
    List<Announcement> announcements =
        IntStream.range(0, 20)
            .mapToObj(
                i -> {
                  Announcement announcement =
                      Announcement.builder().imageUrl("imageUrl" + i).isPinned(true).build();

                  AnnouncementContents translationKo =
                      AnnouncementContents.builder()
                          .announcement(announcement)
                          .languageCode("ko")
                          .title("title" + i)
                          .content("content" + i)
                          .build();

                  announcement.addContents(List.of(translationKo));
                  return announcement;
                })
            .collect(Collectors.toList());

    announcementRepository.saveAll(announcements);

    // when
    List<Announcement> results = announcementRepository.findAllByOrderByCreatedAtDesc();

    // then
    assertThat(results.size()).isEqualTo(20);
    Announcement firstAnnouncement = results.get(0);
    AnnouncementContents firstTranslation =
        firstAnnouncement.getContents().stream()
            .filter(t -> "ko".equals(t.getLanguageCode()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("한국어 번역이 존재하지 않습니다."));

    assertThat(firstTranslation.getTitle()).isEqualTo("title19");
    assertThat(results)
        .isSortedAccordingTo((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
  }

  @Test
  public void 게시글상세조회() {
    // given
    Announcement announcement = Announcement.builder().imageUrl("imageUrl").isPinned(true).build();

    AnnouncementContents translationKo =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("ko")
            .title("타이틀")
            .content("콘텐츠")
            .build();

    AnnouncementContents translationEn =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("en")
            .title("Etitle")
            .content("Econtent")
            .build();

    announcement.addContents(List.of(translationKo, translationEn));
    Announcement savedAnnouncement = announcementRepository.save(announcement);

    // when
    Optional<Announcement> result = announcementRepository.findById(savedAnnouncement.getId());

    // then
    assertThat(result.isPresent()).isTrue();

    Announcement foundAnnouncement = result.get();

    AnnouncementContents koTranslation =
        foundAnnouncement.getContents().stream()
            .filter(t -> "ko".equals(t.getLanguageCode()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("한국어 번역이 존재하지 않습니다."));

    assertThat(koTranslation.getTitle()).isEqualTo("타이틀");
    assertThat(koTranslation.getContent()).isEqualTo("콘텐츠");

    AnnouncementContents enTranslation =
        foundAnnouncement.getContents().stream()
            .filter(t -> "en".equals(t.getLanguageCode()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("영어 번역이 존재하지 않습니다."));

    assertThat(enTranslation.getTitle()).isEqualTo("Etitle");
    assertThat(enTranslation.getContent()).isEqualTo("Econtent");
  }

  @Test
  public void 공지사항검색() {
    // given
    Announcement announcement = Announcement.builder().imageUrl("이미지.jpg").isPinned(true).build();

    AnnouncementContents translationKo =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("ko")
            .title("타이틀")
            .content("컨텐츠")
            .build();

    announcement.addContents(List.of(translationKo));
    announcementRepository.save(announcement);

    List<Announcement> announcements =
        IntStream.range(0, 3)
            .mapToObj(
                i -> {
                  Announcement ann =
                      Announcement.builder().imageUrl("imageUrl" + i).isPinned(true).build();

                  AnnouncementContents translationKoTemp =
                      AnnouncementContents.builder()
                          .announcement(ann)
                          .languageCode("ko")
                          .title("title" + i)
                          .content("content" + i)
                          .build();

                  ann.addContents(List.of(translationKoTemp));
                  return ann;
                })
            .collect(Collectors.toList());
    announcementRepository.saveAll(announcements);

    String keyword = "title";

    // when
    List<Announcement> foundAnnouncements =
        announcementRepository.findByTitleLikeOrderByCreatedAtDesc(keyword);

    // then
    assertThat(foundAnnouncements.size()).isEqualTo(3);
  }
}
