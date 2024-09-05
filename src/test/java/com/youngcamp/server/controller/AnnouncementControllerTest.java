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
        .andExpect(jsonPath("$.data[0].translations[1].title").value("Ktitle0"))
        .andExpect(jsonPath("$.data[0].translations[0].title").value("Etitle0"));
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
            .content("Eontent")
            .build();

    announcement.addContents(List.of(translationKo, translationEn));

    Announcement savedAnnouncement = announcementRepository.save(announcement);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url, savedAnnouncement.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.translations[1].title").value("Ktitle"))
        .andExpect(jsonPath("$.data.translations[0].title").value("Etitle"));
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void 공지사항수정() throws Exception {
    // given
    final String url = "/api/announcements/{announcementId}";

    Announcement oldAnnouncement =
        Announcement.builder().imageUrl("old image").isPinned(false).build();

    Announcement savedAnnouncement = announcementRepository.save(oldAnnouncement);

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

    String json = mapper.writeValueAsString(request);

    // expected
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(url, savedAnnouncement.getId())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(savedAnnouncement.getId()));
  }

  @Test
  public void 공지사항검색() throws Exception {
    // given
    Announcement announcement = Announcement.builder().imageUrl("이미지.jpg").isPinned(true).build();

    AnnouncementContents translationKo =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("ko")
            .title("타이틀")
            .content("컨텐츠")
            .build();

    AnnouncementContents translationEn =
        AnnouncementContents.builder()
            .announcement(announcement)
            .languageCode("en")
            .title("title")
            .content("CEnglish")
            .build();

    announcement.addContents(List.of(translationKo, translationEn));
    announcementRepository.save(announcement);

    List<Announcement> announcements =
        IntStream.range(0, 3)
            .mapToObj(
                i -> {
                  Announcement ann =
                      Announcement.builder().imageUrl("imageUrl" + i).isPinned(true).build();

                  AnnouncementContents koTrans =
                      AnnouncementContents.builder()
                          .announcement(ann)
                          .languageCode("ko")
                          .title("타이틀" + i)
                          .content("컨텐츠" + i)
                          .build();

                  AnnouncementContents enTrans =
                      AnnouncementContents.builder()
                          .announcement(ann)
                          .languageCode("en")
                          .title("title" + i)
                          .content("content" + i)
                          .build();

                  ann.addContents(List.of(koTrans, enTrans));
                  return ann;
                })
            .collect(Collectors.toList());

    announcementRepository.saveAll(announcements);

    final String url = "/api/announcements/search?keyword=title";
    String keyword = "title";

    // expected
    mockMvc
        .perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(3));
  }
}
