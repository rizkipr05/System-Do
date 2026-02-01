package com.doapp.auth;

import com.doapp.auth.dto.AuthResponse;
import com.doapp.auth.dto.LoginRequest;
import com.doapp.auth.dto.RegisterRequest;
import com.doapp.auth.dto.UserDto;
import com.doapp.owner.Owner;
import com.doapp.owner.OwnerRepository;
import com.doapp.user.Role;
import com.doapp.user.User;
import com.doapp.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository userRepo;
  private final OwnerRepository customerRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepo,
                     OwnerRepository customerRepo,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService) {
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public void registerOwner(RegisterRequest req) {
    if (req.name() == null || req.name().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama wajib diisi");

    if (req.email() == null || req.email().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email wajib diisi");

    if (req.password() == null || req.password().length() < 6)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password minimal 6 karakter");

    if (userRepo.existsByEmail(req.email()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email sudah terdaftar");

    User u = new User();
    u.setName(req.name().trim());
    u.setEmail(req.email().trim());
    u.setPhone(req.phone() == null ? null : req.phone().trim());
    u.setRole(Role.CUSTOMER);
    u.setActive(true);
    u.setPasswordHash(passwordEncoder.encode(req.password()));
    userRepo.save(u);

    Owner c = new Owner();
    c.setUser(u);
    c.setOwnerCode("CUST-" + String.format("%04d", u.getId()));
    customerRepo.save(c);
  }

  public AuthResponse login(LoginRequest req) {
    User u = userRepo.findByEmail(req.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email/Password salah"));

    if (!u.isActive())
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun nonaktif");

    if (!passwordEncoder.matches(req.password(), u.getPasswordHash()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email/Password salah");

    String token = jwtService.generate(u.getId(), u.getRole().name(), u.getEmail());

    UserDto userDto = new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole().name());
    return new AuthResponse(token, u.getRole().name(), userDto);
  }
}
