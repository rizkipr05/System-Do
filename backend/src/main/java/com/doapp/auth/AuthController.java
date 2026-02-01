package com.doapp.auth;

import com.doapp.auth.dto.AuthResponse;
import com.doapp.auth.dto.LoginRequest;
import com.doapp.auth.dto.RegisterRequest;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final JwtService jwtService;

  public AuthController(AuthService authService, JwtService jwtService) {
    this.authService = authService;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    authService.registerOwner(req);
    return ResponseEntity.ok(Map.of("message", "Register berhasil"));
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody LoginRequest req) {
    return authService.login(req);
  }

  @GetMapping("/me")
  public Map<String, Object> me(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "").trim();
    Claims c = jwtService.parse(token);

    return Map.of(
        "userId", Long.parseLong(c.getSubject()),
        "role", c.get("role"),
        "email", c.get("email")
    );
  }
}
