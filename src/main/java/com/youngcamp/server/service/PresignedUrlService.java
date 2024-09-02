package com.youngcamp.server.service;

import com.youngcamp.server.dto.PresignedUrlResponseDTO;
import com.youngcamp.server.exception.BadRequestException;
import com.youngcamp.server.utils.ErrorDetail;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class PresignedUrlService {

  @Value("${cloud.aws.s3bucket.id}")
  private String s3AccessId;

  @Value("${cloud.aws.s3bucket.key}")
  private String s3AccessKey;

  @Value("${cloud.aws.s3bucket.name}")
  private String s3BucketName;

  private static final List<String> ALLOWED_IMAGE_TYPES =
      Arrays.asList("image/jpeg", "image/png", "image/gif");

  public PresignedUrlResponseDTO generatePresignedUrl(String objectKey) {
    String mimeType = getMimeType(objectKey);
    String uniqueKey = UUID.randomUUID() + "-" + objectKey;

    if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) {
      List<ErrorDetail> errors =
          Collections.singletonList(
              new ErrorDetail("INVALID_FILE_TYPE", "Only image files are allowed"));
      throw new BadRequestException("Invalid file type", errors);
    }

    AwsCredentialsProvider credentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(s3AccessId, s3AccessKey));

    S3Presigner presigner =
        S3Presigner.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(credentialsProvider)
            .build();

    PutObjectRequest objectRequest =
        PutObjectRequest.builder()
            .bucket(s3BucketName)
            .key(uniqueKey)
            .contentType(mimeType)
            .build();

    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .putObjectRequest(objectRequest)
            .build();

    PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
    return new PresignedUrlResponseDTO(presignedRequest.url().toString(), uniqueKey);
  }

  private String getMimeType(String objectKey) {
    try {
      Path path = Paths.get(objectKey);
      String mimeType = Files.probeContentType(path);
      if (mimeType == null) {
        List<ErrorDetail> errors =
            Collections.singletonList(new ErrorDetail("UNKNOWN_TYPE", "Unknown file type"));
        throw new BadRequestException("Unable to determine MIME type", errors);
      }
      return mimeType;
    } catch (Exception e) {
      List<ErrorDetail> errors =
          Collections.singletonList(new ErrorDetail("ERROR", e.getMessage()));
      throw new BadRequestException("Error determining MIME type", errors);
    }
  }
}
