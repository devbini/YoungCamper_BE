package com.youngcamp.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementDeleteRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementEditRequest;
import com.youngcamp.server.dto.AnnouncementRequest.AnnouncementPostRequest;
import com.youngcamp.server.repository.AnnouncementRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AnnouncementControllerTest {

    @Autowired
    private AnnouncementController target;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

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
        //given
        SecurityContext context = SecurityContextHolder.getContext();
        context.getAuthentication();


        final String url = "/api/announcements";
        AnnouncementPostRequest request = AnnouncementPostRequest.builder()
                .title("title")
                .content("content")
                .imageUrl("image.jpg")
                .isPinned(true)
                .build();
        String json = mapper.writeValueAsString(request);

        //expected
        mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void 공지사항삭제() throws Exception {
        //given
        final String url = "/api/announcements";

        List<Announcement> announcements = IntStream.range(0, 10)
                .mapToObj(i -> Announcement.builder()
                        .title("title" + i)
                        .content("content" + i)
                        .imageUrl("imageUrl" + i)
                        .isPinned(true)
                        .build())
                .collect(Collectors.toList());
        List<Announcement> savedAnnouncements = announcementRepository.saveAll(announcements);

        Long id = announcements.get(0).getId();
        System.out.println(id);

        List<Long> collect = savedAnnouncements.stream().
                map(a -> a.getId())
                .collect(Collectors.toList());
        AnnouncementDeleteRequest request = AnnouncementDeleteRequest.builder()
                .ids(collect)
                .build();
        String json = mapper.writeValueAsString(request);

        //expected
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void 공지사항조회() throws Exception {
        //given
        final String url = "/api/announcements";

        List<Announcement> announcements = IntStream.range(0, 15)
                .mapToObj(i -> Announcement.builder()
                        .title("title" + i)
                        .content("content" + i)
                        .imageUrl("imageUrl" + i)
                        .isPinned(true)
                        .build())
                .collect(Collectors.toList());
        announcementRepository.saveAll(announcements);

        //expected
        mockMvc.perform(
                        MockMvcRequestBuilders.get(url)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(15));
    }

    @Test
    public void 공지사항상세조회() throws Exception {
        //given
        final String url = "/api/announcements/{announcementId}";

        Announcement announcement = Announcement.builder()
                .title("title")
                .content("content")
                .imageUrl("imageUrl")
                .isPinned(true)
                .build();
        Announcement savedAnnouncement = announcementRepository.save(announcement);

        //expected
        mockMvc.perform(
                        MockMvcRequestBuilders.get(url, savedAnnouncement.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("title"));

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void 공지사항수정() throws Exception {
        //given
        final String url = "/api/announcements/{announcementId}";

        Announcement oldAnnouncement = Announcement.builder()
                .title("old title")
                .content("old content")
                .imageUrl("old image")
                .isPinned(false)
                .build();
        Announcement savedAnnouncement = announcementRepository.save(oldAnnouncement);

        AnnouncementEditRequest request = AnnouncementEditRequest.builder()
                .title("new title")
                .content("new content")
                .imageUrl("new image")
                .isPinned(true)
                .build();

        String json = mapper.writeValueAsString(request);

        //expected
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.patch(url, savedAnnouncement.getId())
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.id").value(savedAnnouncement.getId()));

        Announcement result = announcementRepository.findById(savedAnnouncement.getId()).get();
        Assertions.assertThat(result.getTitle()).isEqualTo("new title");
        Assertions.assertThat(result.getContent()).isEqualTo("new content");
        Assertions.assertThat(result.getImageUrl()).isEqualTo("new image");
        Assertions.assertThat(result.getIsPinned()).isEqualTo(true);
    }

    @Test
    public void 공지사항검색() throws Exception {
        //given
        Announcement announcement = Announcement.builder()
                .title("타이틀")
                .content("컨텐츠")
                .imageUrl("이미지.jpg")
                .isPinned(true)
                .build();
        announcementRepository.save(announcement);

        List<Announcement> announcements = IntStream.range(0, 3)
                .mapToObj(i -> Announcement.builder()
                        .title("title" + i)
                        .content("content" + i)
                        .imageUrl("imageUrl" + i)
                        .isPinned(true)
                        .build())
                .collect(Collectors.toList());
        announcementRepository.saveAll(announcements);

        final String url = "/api/announcements/search?keyword=tle";
        String keyword = "tle";

        //expected
        mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));
    }
}
