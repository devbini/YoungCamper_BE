package com.youngcamp.server.dto;

import com.youngcamp.server.validation.ImageUrlPrefix;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
  private String password;

  @Getter
  @Setter
  public static class Review {
    private UUID id;
    private Integer sequence;
    private String content;
    private List<String> imageUrls;
    private String createdAt;
    private String updatedAt;
  }

  @Getter
  @Setter
  public static class Reviews {
    private List<Review> reviews;
  }

  @Getter
  @Setter
  public static class PostReviewRequest {

    @Size(min = 4, max = 8)
    private String password;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 10)
    private String content;

    @ImageUrlPrefix private List<String> imageUrls;
  }

  @Getter
  @Setter
  public static class UpdateReviewRequest {
    @Size(min = 4, max = 8)
    private String password;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 10)
    private String content;

    @ImageUrlPrefix private List<String> imageUrls;
  }

  @Getter
  @Setter
  public static class DeleteReviewRequest {
    private String password;
  }
}
