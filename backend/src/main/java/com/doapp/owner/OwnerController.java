package com.doapp.owner;

import com.doapp.auth.AuthHelper;
import com.doapp.owner.dto.ProfileResponse;
import com.doapp.owner.dto.UpdateProfileRequest;
import com.doapp.user.User;
import com.doapp.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customer")
public class OwnerController {
  private final AuthHelper authHelper;
  private final UserRepository userRepo;
  private final OwnerRepository customerRepo;

  public OwnerController(AuthHelper authHelper, UserRepository userRepo, OwnerRepository customerRepo) {
    this.authHelper = authHelper;
    this.userRepo = userRepo;
    this.customerRepo = customerRepo;
  }

  @GetMapping("/me")
  public ProfileResponse me(@RequestHeader("Authorization") String authHeader) {
    Owner c = authHelper.requireOwner(authHeader);
    User u = c.getUser();

    return new ProfileResponse(
        u.getId(),
        u.getName(),
        u.getEmail(),
        u.getPhone(),
        c.getOwnerCode(),
        c.getCompanyName()
    );
  }

  @PutMapping("/me")
  public ProfileResponse update(@RequestHeader("Authorization") String authHeader,
                                @RequestBody UpdateProfileRequest req) {
    Owner c = authHelper.requireOwner(authHeader);
    User u = c.getUser();

    if (req.name() == null || req.name().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama wajib diisi");

    u.setName(req.name().trim());
    u.setPhone(req.phone() == null ? null : req.phone().trim());
    userRepo.save(u);

    c.setCompanyName(req.companyName() == null ? null : req.companyName().trim());
    customerRepo.save(c);

    return new ProfileResponse(
        u.getId(),
        u.getName(),
        u.getEmail(),
        u.getPhone(),
        c.getOwnerCode(),
        c.getCompanyName()
    );
  }
}
