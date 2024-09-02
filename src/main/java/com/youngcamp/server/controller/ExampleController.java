package com.youngcamp.server.controller;

import com.youngcamp.server.exception.NotFoundException;
import com.youngcamp.server.service.AdminChecker;
import com.youngcamp.server.utils.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

  @Operation(
      summary = "Retrieve resource by ID",
      description = "This endpoint retrieves a resource by its ID.")
  @GetMapping("/api/resource")
  public SuccessResponse<String> getResource(@RequestParam(required = false) String id) {
    if (id == null) {
      throw new IllegalArgumentException("ID parameter is required");
    }

    switch (id) {
      case "notfound":
        throw new NotFoundException("Resource", id, "Resource with the specified ID was not found");
      case "unauthorized":
        throw new BadCredentialsException("Invalid credentials");
      case "forbidden":
        throw new AccessDeniedException("Access is denied");
    }

    String resourceData = "Resource with ID: " + id;
    return new SuccessResponse<>("Resource found", resourceData);
  }

  @Autowired private AdminChecker adminChecker;

  @GetMapping("/api/admincheck")
  public Boolean AdminCheckControl() {
    return adminChecker.isAdmin();
  }
}
