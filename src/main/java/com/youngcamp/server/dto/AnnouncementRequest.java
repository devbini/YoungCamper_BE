package com.youngcamp.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AnnouncementRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementPostRequest {
        private String title;
        private String content;
        private String imageUrl;
        private String fileUrl;
        private Boolean isPinned;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementDeleteRequest{
        private List<Long> ids;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AnnouncementEditRequest {
        private String title;
        private String content;
        private String imageUrl;
        private String fileUrl;
        private Boolean isPinned;
    }
}
