package com.youngcamp.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

  @Autowired private MockMvc mockMvc;

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
  public void testGetTest() throws Exception {
    mockMvc
        .perform(get("/test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("test success"));
  }
}
