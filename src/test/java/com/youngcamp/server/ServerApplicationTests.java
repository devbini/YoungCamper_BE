package com.youngcamp.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class ServerApplicationTests {

  @Autowired private DataSource dataSource;

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private MockMvc mockMvc;

  @Value("${server.address}")
  private String serverAddress;

  @Value("${server.port}")
  private int serverPort;

  @Value("${auth.username}")
  private String username;

  @Value("${auth.password}")
  private String password;

  @Test
  void contextLoads() {
    // 애플리케이션 컨텍스트 로드 여부를 확인하는 기본 테스트
    assertThat(dataSource).isNotNull();
  }

  @Test
  void testDatabaseConnection() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      assertThat(connection.isValid(1)).isTrue();
    }
  }

  @Test
  void testSwaggerUI() {
    // Swagger UI에 접근하기 위한 URL 설정
    String url = String.format("http://%s:%d/api/login", serverAddress, port); // 로그인 경로 변경

    // HTTP Basic 인증 헤더 추가
    HttpHeaders headers = new HttpHeaders();
    String auth = username + ":" + password;
    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
    String authHeader = "Basic " + encodedAuth;
    headers.set("Authorization", authHeader);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    // 로그인 후 Swagger UI 접근
    String swaggerUrl =
        String.format("http://%s:%d/api/swagger-ui/index.html", serverAddress, port);
    ResponseEntity<String> swaggerResponse =
        this.restTemplate.exchange(swaggerUrl, HttpMethod.GET, entity, String.class);

    assertThat(swaggerResponse.getBody()).contains("Swagger UI");
  }

  @Test
  public void testGetTest() throws Exception {
    mockMvc
        .perform(get("/test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("test success"));
  }
}
