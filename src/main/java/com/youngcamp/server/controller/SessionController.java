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
  }
}
