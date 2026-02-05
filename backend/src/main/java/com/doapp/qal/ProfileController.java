package com.doapp.qal;

import com.doapp.auth.AuthHelper;
import com.doapp.qal.dto.ProfileDto;
import com.doapp.user.User;
import com.doapp.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/qal/profile")
public class ProfileController {
  private final AuthHelper authHelper;
  private final UserRepository userRepo;
  private final AdminProfileRepository adminProfileRepo;
  private final CustomerProfileRepository customerProfileRepo;
  private final DriverProfileRepository driverProfileRepo;
  private final PasswordEncoder passwordEncoder;

  public ProfileController(AuthHelper authHelper,
                           UserRepository userRepo,
                           AdminProfileRepository adminProfileRepo,
                           CustomerProfileRepository customerProfileRepo,
                           DriverProfileRepository driverProfileRepo,
                           PasswordEncoder passwordEncoder) {
    this.authHelper = authHelper;
    this.userRepo = userRepo;
    this.adminProfileRepo = adminProfileRepo;
    this.customerProfileRepo = customerProfileRepo;
    this.driverProfileRepo = driverProfileRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/admin")
  public ProfileDto getAdmin(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireAdmin(authHeader);
    AdminProfile profile = adminProfileRepo.findByUserId(user.getId()).orElse(null);
    return new ProfileDto("ADMIN", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile == null ? null : profile.getAdminCode(),
        profile == null ? null : profile.getPosition(),
        null);
  }

  @PutMapping("/admin")
  public ProfileDto updateAdmin(@RequestHeader("Authorization") String authHeader,
                             @RequestBody ProfileDto req) {
    User user = authHelper.requireAdmin(authHeader);
    if (req.name() != null && !req.name().isBlank()) user.setName(req.name().trim());
    if (req.email() != null && !req.email().isBlank() && !req.email().equalsIgnoreCase(user.getEmail())) {
      if (userRepo.existsByEmail(req.email().trim()))
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah digunakan");
      user.setEmail(req.email().trim());
    }
    if (req.phone() != null) user.setPhone(req.phone().trim());
    if (req.password() != null && !req.password().isBlank()) {
      if (req.password().length() < 6)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password minimal 6 karakter");
      user.setPasswordHash(passwordEncoder.encode(req.password()));
    }
    userRepo.save(user);

    AdminProfile profile = adminProfileRepo.findByUserId(user.getId()).orElse(null);
    String code = req.code() == null ? null : req.code().trim();
    String position = req.position() == null ? null : req.position().trim();
    if ((code == null || code.isBlank()) && profile == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Admin wajib diisi");
    if ((position == null || position.isBlank()) && profile == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jabatan Admin wajib diisi");

    if (profile == null) profile = new AdminProfile();
    profile.setUser(user);
    if (code != null && !code.isBlank()) profile.setAdminCode(code);
    if (position != null && !position.isBlank()) profile.setPosition(position);
    profile = adminProfileRepo.save(profile);

    return new ProfileDto("ADMIN", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile.getAdminCode(), profile.getPosition(), null);
  }

  @GetMapping("/driver")
  public ProfileDto getDriver(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireDriver(authHeader);
    DriverProfile profile = driverProfileRepo.findByUserId(user.getId()).orElse(null);
    return new ProfileDto("DRIVER", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile == null ? null : profile.getDriverCode(), null, null);
  }

  @PutMapping("/driver")
  public ProfileDto updateDriver(@RequestHeader("Authorization") String authHeader,
                                 @RequestBody ProfileDto req) {
    User user = authHelper.requireDriver(authHeader);
    if (req.name() != null && !req.name().isBlank()) user.setName(req.name().trim());
    if (req.email() != null && !req.email().isBlank() && !req.email().equalsIgnoreCase(user.getEmail())) {
      if (userRepo.existsByEmail(req.email().trim()))
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah digunakan");
      user.setEmail(req.email().trim());
    }
    if (req.phone() != null) user.setPhone(req.phone().trim());
    if (req.password() != null && !req.password().isBlank()) {
      if (req.password().length() < 6)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password minimal 6 karakter");
      user.setPasswordHash(passwordEncoder.encode(req.password()));
    }
    userRepo.save(user);

    DriverProfile profile = driverProfileRepo.findByUserId(user.getId()).orElse(null);
    String code = req.code() == null ? null : req.code().trim();
    if ((code == null || code.isBlank()) && profile == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Driver wajib diisi");

    if (profile == null) profile = new DriverProfile();
    profile.setUser(user);
    if (code != null && !code.isBlank()) profile.setDriverCode(code);
    profile = driverProfileRepo.save(profile);

    return new ProfileDto("DRIVER", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile.getDriverCode(), null, null);
  }

  @GetMapping("/customer")
  public ProfileDto getCustomer(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireCustomer(authHeader).getUser();
    CustomerProfile profile = customerProfileRepo.findByUserId(user.getId()).orElse(null);
    return new ProfileDto("CUSTOMER", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile == null ? null : profile.getCustomerCode(), null, null);
  }

  @PutMapping("/customer")
  public ProfileDto updateCustomer(@RequestHeader("Authorization") String authHeader,
                                @RequestBody ProfileDto req) {
    User user = authHelper.requireCustomer(authHeader).getUser();
    if (req.name() != null && !req.name().isBlank()) user.setName(req.name().trim());
    if (req.email() != null && !req.email().isBlank() && !req.email().equalsIgnoreCase(user.getEmail())) {
      if (userRepo.existsByEmail(req.email().trim()))
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah digunakan");
      user.setEmail(req.email().trim());
    }
    if (req.phone() != null) user.setPhone(req.phone().trim());
    if (req.password() != null && !req.password().isBlank()) {
      if (req.password().length() < 6)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password minimal 6 karakter");
      user.setPasswordHash(passwordEncoder.encode(req.password()));
    }
    userRepo.save(user);

    CustomerProfile profile = customerProfileRepo.findByUserId(user.getId()).orElse(null);
    String code = req.code() == null ? null : req.code().trim();
    if ((code == null || code.isBlank()) && profile == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Customer wajib diisi");

    if (profile == null) profile = new CustomerProfile();
    profile.setUser(user);
    if (code != null && !code.isBlank()) profile.setCustomerCode(code);
    profile = customerProfileRepo.save(profile);

    return new ProfileDto("CUSTOMER", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile.getCustomerCode(), null, null);
  }
}
