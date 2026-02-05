package com.doapp.auth;

import com.doapp.customer.Customer;
import com.doapp.customer.CustomerRepository;
import com.doapp.user.Role;
import com.doapp.user.User;
import com.doapp.user.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthHelper {
  private final JwtService jwtService;
  private final UserRepository userRepo;
  private final CustomerRepository customerRepo;

  public AuthHelper(JwtService jwtService, UserRepository userRepo, CustomerRepository customerRepo) {
    this.jwtService = jwtService;
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
  }

  public User requireUser(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer "))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token tidak valid");

    String token = authHeader.replace("Bearer ", "").trim();
    Claims claims = jwtService.parse(token);
    Long userId = Long.parseLong(claims.getSubject());

    return userRepo.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tidak ditemukan"));
  }

  public Customer requireCustomer(String authHeader) {
    User user = requireUser(authHeader);
    return customerRepo.findByUserId(user.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer tidak ditemukan"));
  }

  public User requireAdmin(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer "))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token tidak valid");

    String token = authHeader.replace("Bearer ", "").trim();
    Claims claims = jwtService.parse(token);
    String role = String.valueOf(claims.get("role"));
    if (!Role.ADMIN.name().equals(role))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akses Admin diperlukan");

    Long userId = Long.parseLong(claims.getSubject());
    return userRepo.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tidak ditemukan"));
  }

  public User requireDriver(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer "))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token tidak valid");

    String token = authHeader.replace("Bearer ", "").trim();
    Claims claims = jwtService.parse(token);
    String role = String.valueOf(claims.get("role"));
    if (!Role.DRIVER.name().equals(role))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akses Driver diperlukan");

    Long userId = Long.parseLong(claims.getSubject());
    return userRepo.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tidak ditemukan"));
  }
}
