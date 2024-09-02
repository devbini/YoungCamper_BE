package com.youngcamp.server.service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@SpringBootTest
@ActiveProfiles("test")
class TestUploadService {

  private static final Logger logger = LoggerFactory.getLogger(TestUploadService.class);

  @Value("${cloud.aws.s3bucket.id}")
  private String s3AccessId;

  @Value("${cloud.aws.s3bucket.key}")
  private String s3AccessKey;

  @Value("${cloud.aws.s3bucket.bucketname}")
  private String s3BucketName;

  @Test
  void getPreSignedUrl() {

    try {
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
              .key("test2")
              .contentType("text/plain")
              .build();

      PutObjectPresignRequest presignRequest =
          PutObjectPresignRequest.builder()
              .signatureDuration(Duration.ofMinutes(2))
              .putObjectRequest(objectRequest)
              .build();

      PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

      String myURL = presignedRequest.url().toString();
      logger.info("Presigned URL to upload a file to: {}", myURL);
      logger.info(
          "Which HTTP method needs to be used when uploading a file: {}",
          presignedRequest.httpRequest().method());

      URL url = presignedRequest.url();

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "text/plain");
      connection.setRequestMethod("PUT");
      OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
      out.write("This text was uploaded as an object by using a presigned URL.");
      out.close();

      connection.getResponseCode();
      logger.info("HTTP response code is {}", connection.getResponseCode());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
