package com.doapp.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
  @Value("${app.jwt.secret}")
  private String secret;

  public String generate(Long userId, String role, String email) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + 1000L * 60 * 60 * 12);

    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("role", role)
        .claim("email", email)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
