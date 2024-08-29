<<<<<<< HEAD
package com.youngcamp.server.controller;

import com.youngcamp.server.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class SessionController {

  @Autowired private SessionService sessionService;

  @PostMapping("/login")
  public String login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
    String id = loginRequest.get("id");
    String pw = loginRequest.get("pw");

    return sessionService.authenticate(id, pw, request);
=======
/*
 * **********************************
 * 파일명 : SessionController.java
 * 작성일(수정일) : 2024-08-28
 * 작성자 : 김찬빈
 * -
 * 파일 역할
 * 1. /ses/login & /ses/logout 메서드를 service 패키지로 서빙합니다.
 * 2. Post 형태로 제공받을 수 있도록 REST 역할을 수행합니다.
 * **********************************
 */

package com.youngcamp.server.controller;

import com.youngcamp.server.dto.SessionReq;
import com.youngcamp.server.service.SessionService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ses") // 차후 변경 가능
public class SessionController {
  // 단일성 유지
  private final SessionService sessionService;

  // 생성자 (의존성 주입)
  public SessionController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  // Login 요청 처리
  @PostMapping("/login")
  @ResponseBody
  public Map<String, String> login(@RequestBody SessionReq request, HttpSession session) {
    boolean isSuccess = sessionService.login(request.getID(), request.getPassword());
    Map<String, String> response = new HashMap<>();

    // 로그인 성공시
    if (isSuccess) {
      // 세션에 로그인 성공 상태 저장
      session.setAttribute("isLoggedIn", true);
      response.put("success", "Logged Success");
      return response;
    } else { // 로그인 실패시
      response.put("error", "Logged Fail");
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Failed", new Exception(response.toString()));
    }
  }

  // Logout 요청 처리
  @PostMapping("/logout")
  @ResponseBody
  public Map<String, String> logout(HttpSession session) {
    // 세션 로그아웃
    sessionService.logout(session);

    Map<String, String> response = new HashMap<>();
    response.put("success", "Logout Success");
    return response;
>>>>>>> 9125423142438ea491d8d14359e302a8c2b0221f
  }
}
