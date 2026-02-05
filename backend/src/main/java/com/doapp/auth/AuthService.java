package com.doapp.auth;

import com.doapp.auth.dto.AuthResponse;
import com.doapp.auth.dto.LoginRequest;
import com.doapp.auth.dto.RegisterRequest;
import com.doapp.auth.dto.UserDto;
import com.doapp.customer.Customer;
import com.doapp.customer.CustomerRepository;
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
  private final CustomerRepository customerRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepo,
                     CustomerRepository customerRepo,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService) {
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public void registerCustomer(RegisterRequest req) {
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

    Customer c = new Customer();
    c.setUser(u);
    c.setCustomerCode("CUST-" + String.format("%04d", u.getId()));
    customerRepo.save(c);
  }

  public AuthResponse login(LoginRequest req) {
    User u = userRepo.findByEmail(req.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email/Password salah"));

    if (!u.isActive())
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun nonaktif");

    if (!passwordEncoder.matches(req.password(), u.getPasswordHash()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email/Password salah");

    String roleName = u.getRole().name();
    String token = jwtService.generate(u.getId(), roleName, u.getEmail());

    UserDto userDto = new UserDto(u.getId(), u.getName(), u.getEmail(), roleName);
    return new AuthResponse(token, roleName, userDto);
  }
}
