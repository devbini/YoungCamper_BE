package com.youngcamp.server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.domain.AnnouncementContents;
import com.youngcamp.server.dto.AnnouncementRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementDeleteRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementEditRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementPostRequest;
import com.youngcamp.server.repository.AnnouncementRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@Transactional
public class AnnouncementControllerTest {

  @Autowired private AnnouncementController target;

  @Autowired private AnnouncementRepository announcementRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper mapper;

  @AfterEach
  void clean() {
    announcementRepository.deleteAll();
  }

  @Test
  public void mockMvc가Null이아님() {
    assertThat(target).isNotNull();
    assertThat(mockMvc).isNotNull();
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void 공지사항등록() throws Exception {
    // given
    SecurityContext context = SecurityContextHolder.getContext();
    context.getAuthentication();

    final String url = "/api/announcements";
    AnnouncementPostRequest request =
        AnnouncementPostRequest.builder()
            .imageUrl("image.jpg")
            .isPinned(true)
            .contents(
                List.of(
                    AnnouncementRequest.AnnouncementTrRequest.builder()
                        .languageCode("ko")
                        .title("타이틀")
                        .content("컨텐츠")
                        .build(),
                    AnnouncementRequest.AnnouncementTrRequest.builder()
                        .languageCode("en")
                        .title("Etitle")
                        .content("Econtent")
                        .build()))
            .build();
    String json = mapper.writeValueAsString(request);

    // expected
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(url).content(json).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void 공지사항삭제() throws Exception {
    // given
    final String url = "/api/announcements";

    List<Announcement> announcements =
        IntStream.range(0, 10)
            .mapToObj(
                i -> {
                  Announcement announcement =
                      Announcement.builder().imageUrl("imageUrl" + i).isPinned(true).build();

                  AnnouncementContents translationKo =
                      AnnouncementContents.builder()
                          .announcement(announcement)
                          .languageCode("ko")
                          .title("타이틀" + i)
                          .content("컨텐츠" + i)
                          .build();

                  AnnouncementContents translationEn =
                      AnnouncementContents.builder()
                          .announcement(announcement)
                          .languageCode("en")
                          .title("Etitle" + i)
                          .content("Econtent" + i)
                          .build();

                  announcement.addContents(List.of(translationKo, translationEn));
                  return announcement;
                })
            .collect(Collectors.toList());

    List<Announcement> savedAnnouncements = announcementRepository.saveAll(announcements);

    System.out.println(savedAnnouncements);
    List<Long> ids =
        savedAnnouncements.stream().map(Announcement::getId).collect(Collectors.toList());
    AnnouncementDeleteRequest request = AnnouncementDeleteRequest.builder().ids(ids).build();
    String json = mapper.writeValueAsString(request);

    // expected
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(url)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // 삭제 확인
    List<Announcement> remainingAnnouncements = announcementRepository.findAll();
    System.out.println(remainingAnnouncements);
    assertThat(remainingAnnouncements).isEmpty();
  }

  @Test
  public void 공지사항조회() throws Exception {
    // given
    final String url = "/api/announcements";

    List<Announcement> announcements =
        IntStream.range(0, 15)
            .mapToObj(
                i -> {
                  Announcement announcement =
                      Announcement.builder().imageUrl("imageUrl" + i).isPinned(true).build();

                  AnnouncementContents translationKo =
                      AnnouncementContents.builder()
                          .announcement(announcement)
                          .languageCode("ko")
                          .title("타이틀" + i)
                          .content("컨텐츠" + i)
                          .build();

                  AnnouncementContents translationEn =
                      AnnouncementContents.builder()
                          .announcement(announcement)
                          .languageCode("en")
                          .title("Etitle" + i)
                          .content("Econtent" + i)
                          .build();

                  announcement.addContents(List.of(translationKo, translationEn));
                  return announcement;
                })
            .collect(Collectors.toList());

    announcementRepository.saveAll(announcements);

    // expected
    mockMvc
        .perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(15))
        .andExpect(jsonPath("$.data[0].content.title").value("타이틀14")); // 단일 언어 처리
  }

  @Test
  public void 공지사항상세조회() throws Exception {
    // given
    final String url = "/api/announcements/{announcementId}";

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

    // save announcement and get saved instance with id
    Announcement savedAnnouncement = announcementRepository.save(announcement);

    Hibernate.initialize(savedAnnouncement.getContents());
    // when and then
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url, savedAnnouncement.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content.languageCode").value("ko"))
        .andExpect(jsonPath("$.data.content.title").value("타이틀"))
        .andExpect(jsonPath("$.data.content.content").value("콘텐츠"));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void 공지사항수정() throws Exception {
    // given
    final String url = "/api/announcements/{announcementId}";

    // 기존 공지사항 생성
    Announcement oldAnnouncement =
        Announcement.builder().imageUrl("old image").isPinned(false).build();

    // 공지사항 저장
    Announcement savedAnnouncement = announcementRepository.save(oldAnnouncement);

    // 수정할 내용 준비
    AnnouncementEditRequest request =
        AnnouncementEditRequest.builder()
            .imageUrl("new image")
            .isPinned(true)
            .contents(
                List.of(
                    AnnouncementRequest.AnnouncementTrRequest.builder()
                        .languageCode("ko")
                        .title("신규 타이틀")
                        .content("신규 콘텐츠")
                        .build(),
                    AnnouncementRequest.AnnouncementTrRequest.builder()
                        .languageCode("en")
                        .title("New Etitle")
                        .content("New Econtent")
                        .build()))
            .build();

    // 요청을 JSON 형식으로 변환
    String json = mapper.writeValueAsString(request);

    // expected
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(url, savedAnnouncement.getId())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(savedAnnouncement.getId()))
        .andExpect(jsonPath("$.data.imageUrl").value("new image"))
        .andExpect(jsonPath("$.data.isPinned").value(true))
        .andExpect(jsonPath("$.data.contents[?(@.languageCode == 'ko')].title").value("신규 타이틀"))
        .andExpect(
            jsonPath("$.data.contents[?(@.languageCode == 'en')].title").value("New Etitle"));
  }
}
