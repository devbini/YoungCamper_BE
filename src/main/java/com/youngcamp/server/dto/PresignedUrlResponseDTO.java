package com.youngcamp.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PresignedUrlResponseDTO {

  private String presignedUrl;

  public PresignedUrlResponseDTO(String presignedUrl) {
    this.presignedUrl = presignedUrl;
  }
}
