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
  private final QcProfileRepository qcProfileRepo;
  private final OwnerProfileRepository ownerProfileRepo;
  private final ProjectControlProfileRepository pcProfileRepo;
  private final PasswordEncoder passwordEncoder;

  public ProfileController(AuthHelper authHelper,
                           UserRepository userRepo,
                           QcProfileRepository qcProfileRepo,
                           OwnerProfileRepository ownerProfileRepo,
                           ProjectControlProfileRepository pcProfileRepo,
                           PasswordEncoder passwordEncoder) {
    this.authHelper = authHelper;
    this.userRepo = userRepo;
    this.qcProfileRepo = qcProfileRepo;
    this.ownerProfileRepo = ownerProfileRepo;
    this.pcProfileRepo = pcProfileRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/qc")
  public ProfileDto getQc(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireQualityControl(authHeader);
    QcProfile profile = qcProfileRepo.findByUserId(user.getId()).orElse(null);
    return new ProfileDto("QC", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile == null ? null : profile.getQcCode(),
        profile == null ? null : profile.getPosition(),
        null);
  }

  @PutMapping("/qc")
  public ProfileDto updateQc(@RequestHeader("Authorization") String authHeader,
                             @RequestBody ProfileDto req) {
    User user = authHelper.requireQualityControl(authHeader);
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

    if (req.code() == null || req.code().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID QC wajib diisi");
    if (req.position() == null || req.position().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jabatan QC wajib diisi");

    QcProfile profile = qcProfileRepo.findByUserId(user.getId()).orElse(new QcProfile());
    profile.setUser(user);
    profile.setQcCode(req.code().trim());
    profile.setPosition(req.position().trim());
    profile = qcProfileRepo.save(profile);

    return new ProfileDto("QC", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile.getQcCode(), profile.getPosition(), null);
  }

  @GetMapping("/pc")
  public ProfileDto getPc(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireProjectControl(authHeader);
    ProjectControlProfile profile = pcProfileRepo.findByUserId(user.getId()).orElse(null);
    return new ProfileDto("PROJECT_CONTROL", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile == null ? null : profile.getPcCode(), null, null);
  }

  @PutMapping("/pc")
  public ProfileDto updatePc(@RequestHeader("Authorization") String authHeader,
                             @RequestBody ProfileDto req) {
    User user = authHelper.requireProjectControl(authHeader);
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

    if (req.code() == null || req.code().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Project Control wajib diisi");

    ProjectControlProfile profile = pcProfileRepo.findByUserId(user.getId()).orElse(new ProjectControlProfile());
    profile.setUser(user);
    profile.setPcCode(req.code().trim());
    profile = pcProfileRepo.save(profile);

    return new ProfileDto("PROJECT_CONTROL", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile.getPcCode(), null, null);
  }

  @GetMapping("/owner")
  public ProfileDto getOwner(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireOwner(authHeader).getUser();
    OwnerProfile profile = ownerProfileRepo.findByUserId(user.getId()).orElse(null);
    return new ProfileDto("OWNER", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile == null ? null : profile.getOwnerCode(), null, null);
  }

  @PutMapping("/owner")
  public ProfileDto updateOwner(@RequestHeader("Authorization") String authHeader,
                                @RequestBody ProfileDto req) {
    User user = authHelper.requireOwner(authHeader).getUser();
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

    if (req.code() == null || req.code().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Owner wajib diisi");

    OwnerProfile profile = ownerProfileRepo.findByUserId(user.getId()).orElse(new OwnerProfile());
    profile.setUser(user);
    profile.setOwnerCode(req.code().trim());
    profile = ownerProfileRepo.save(profile);

    return new ProfileDto("OWNER", user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        profile.getOwnerCode(), null, null);
  }
}
