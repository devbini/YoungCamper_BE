package com.youngcamp.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PresignedUrlResponseDTO {

  private String presignedUrl;
  private String fileName;

  public PresignedUrlResponseDTO(String presignedUrl, String uniqueKey) {
    this.presignedUrl = presignedUrl;
    this.fileName = uniqueKey;
  }
}
