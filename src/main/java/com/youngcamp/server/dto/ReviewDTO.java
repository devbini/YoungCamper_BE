package com.youngcamp.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

public class ReviewDTO {

  private UUID id;
  private Integer sequence;
  private String content;
  private List<String> imageUrls;
  private String createdAt;
  private String updatedAt;
  @JsonIgnore private String password;

  @Getter
  @Setter
  public static class Reviews {}

  @Getter
  @Setter
  public static class PostReviewRequest {
    private String password;
    private String content;
    private List<String> imageUrls;
  }

  @Getter
  @Setter
  public static class UpdateReviewRequest {
    private String password;
    private String content;
    private List<String> imageUrls;
  }

  @Getter
  @Setter
  public static class DeleteReviewRequest {
    private String password;
  }
}
