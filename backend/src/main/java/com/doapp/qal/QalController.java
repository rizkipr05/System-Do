package com.doapp.qal;

import com.doapp.auth.AuthHelper;
import com.doapp.qal.dto.QalCreateRequest;
import com.doapp.qal.dto.QalDetailDto;
import com.doapp.qal.dto.QalDto;
import com.doapp.qal.dto.CustomerProfileDto;
import com.doapp.qal.dto.DriverProfileDto;
import com.doapp.qal.dto.AdminProfileDto;
import com.doapp.qal.dto.SpkDto;
import com.doapp.qal.dto.UserCreateRequest;
import com.doapp.qal.dto.UserLiteDto;
import com.doapp.user.Role;
import com.doapp.user.User;
import com.doapp.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/qal")
public class QalController {
  private final AuthHelper authHelper;
  private final QalRepository qalRepo;
  private final QalDetailRepository detailRepo;
  private final SpkRepository spkRepo;
  private final AdminProfileRepository adminProfileRepo;
  private final CustomerProfileRepository customerProfileRepo;
  private final DriverProfileRepository driverProfileRepo;
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;

  public QalController(AuthHelper authHelper,
                       QalRepository qalRepo,
                       QalDetailRepository detailRepo,
                       SpkRepository spkRepo,
                       AdminProfileRepository adminProfileRepo,
                       CustomerProfileRepository customerProfileRepo,
                       DriverProfileRepository driverProfileRepo,
                       UserRepository userRepo,
                       PasswordEncoder passwordEncoder) {
    this.authHelper = authHelper;
    this.qalRepo = qalRepo;
    this.detailRepo = detailRepo;
    this.spkRepo = spkRepo;
    this.adminProfileRepo = adminProfileRepo;
    this.customerProfileRepo = customerProfileRepo;
    this.driverProfileRepo = driverProfileRepo;
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public List<QalDto> list(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireUser(authHeader);
    if (user.getRole() == Role.ADMIN) {
      return qalRepo.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }
    if (user.getRole() == Role.DRIVER) {
      return qalRepo.findByDriverUserIdOrderByCreatedAtDesc(user.getId())
          .stream().map(this::toDto).toList();
    }
    if (user.getRole() == Role.CUSTOMER) {
      return qalRepo.findByCustomerUserIdOrderByCreatedAtDesc(user.getId())
          .stream().map(this::toDto).toList();
    }
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akses tidak diizinkan");
  }

  @GetMapping("/{id}")
  public QalDto detail(@RequestHeader("Authorization") String authHeader,
                       @PathVariable String id) {
    User user = authHelper.requireUser(authHeader);
    QalRecord qal = qalRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QAL tidak ditemukan"));
    if (!canAccess(user, qal))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akses tidak diizinkan");
    return toDto(qal);
  }

  @PostMapping
  public QalDto create(@RequestHeader("Authorization") String authHeader,
                       @RequestBody QalCreateRequest req) {
    User admin = authHelper.requireAdmin(authHeader);
    if (req.qalNumber() == null || req.qalNumber().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No QAL wajib diisi");
    if (req.qalDate() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tanggal QAL wajib diisi");
    if (req.spkNumber() == null || req.spkNumber().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SPK wajib diisi");
    if (req.jobName() == null || req.jobName().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama pekerjaan wajib diisi");

    String qalNumber = req.qalNumber().trim();
    if (qalRepo.findByQalNumber(qalNumber).isPresent())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No QAL sudah digunakan");

    Spk spk = spkRepo.findBySpkNumber(req.spkNumber().trim())
        .orElseGet(() -> {
          Spk s = new Spk();
          s.setSpkNumber(req.spkNumber().trim());
          s.setJobName(req.jobName().trim());
          return spkRepo.save(s);
        });

    AdminProfile adminProfile = adminProfileRepo.findByUserId(admin.getId()).orElse(null);
    if (adminProfile == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profil Admin belum diisi");
    }

    User driver = null;
    DriverProfile driverProfile = null;
    if (req.driverUserId() != null) {
      driver = userRepo.findById(req.driverUserId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver tidak ditemukan"));
      driverProfile = driverProfileRepo.findByUserId(driver.getId()).orElse(null);
      if (driverProfile == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profil Driver belum diisi");
      }
    }
    User customer = null;
    if (req.customerUserId() != null) {
      customer = userRepo.findById(req.customerUserId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer tidak ditemukan"));
    }
    CustomerProfile customerProfile = null;
    if (customer != null) {
      customerProfile = customerProfileRepo.findByUserId(customer.getId()).orElse(null);
      if (customerProfile == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profil Customer belum diisi");
      }
    }

    QalRecord qal = new QalRecord();
    qal.setQalNumber(qalNumber);
    qal.setQalDate(req.qalDate());
    qal.setSpk(spk);
    qal.setAdminUser(admin);
    qal.setAdminCode(adminProfile.getAdminCode());
    qal.setAdminPosition(adminProfile.getPosition());
    qal.setDriverUser(driver);
    qal.setDriverCode(driverProfile == null ? null : driverProfile.getDriverCode());
    qal.setDriverName(driver == null ? null : driver.getName());
    qal.setCustomerUser(customer);
    qal.setCustomerCode(customerProfile == null ? null : customerProfile.getCustomerCode());
    qal.setCustomerName(customer == null ? null : customer.getName());
    qal.setStatus(QalStatus.DRAFT);
    qal = qalRepo.save(qal);

    if (req.details() != null) {
      for (QalDetailDto d : req.details()) {
        if (d.documentName() == null || d.documentName().isBlank()) continue;
        QalDetail detail = new QalDetail();
        detail.setQal(qal);
        detail.setDocumentName(d.documentName().trim());
        detail.setDocumentType(safe(d.documentType(), 80, "Lainnya"));
        detail.setReceivedDate(d.receivedDate());
        detail.setVerificationStatus(safe(d.verificationStatus(), 40, "Disetujui"));
        detailRepo.save(detail);
      }
    }

    return toDto(qal);
  }

  @PostMapping("/{id}/sign")
  public QalDto sign(@RequestHeader("Authorization") String authHeader,
                     @PathVariable String id) {
    User driver = authHelper.requireDriver(authHeader);
    QalRecord qal = qalRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QAL tidak ditemukan"));
    if (qal.getStatus() != QalStatus.DRAFT)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status tidak valid untuk TTD");
    if (qal.getDriverUser() == null) {
      qal.setDriverUser(driver);
      DriverProfile driverProfile = driverProfileRepo.findByUserId(driver.getId()).orElse(null);
      if (driverProfile != null) {
        qal.setDriverCode(driverProfile.getDriverCode());
      }
      qal.setDriverName(driver.getName());
    } else if (!qal.getDriverUser().getId().equals(driver.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bukan Driver terkait");
    }
    qal.setStatus(QalStatus.SIGNED);
    qal.setSignedAt(Instant.now());
    return toDto(qalRepo.save(qal));
  }

  @PostMapping("/{id}/approve")
  public QalDto approve(@RequestHeader("Authorization") String authHeader,
                        @PathVariable String id) {
    User customer = authHelper.requireCustomer(authHeader).getUser();
    QalRecord qal = qalRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QAL tidak ditemukan"));
    if (qal.getStatus() != QalStatus.SIGNED)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status tidak valid untuk ACC");
    if (qal.getCustomerUser() == null) {
      qal.setCustomerUser(customer);
      CustomerProfile customerProfile = customerProfileRepo.findByUserId(customer.getId()).orElse(null);
      if (customerProfile != null) {
        qal.setCustomerCode(customerProfile.getCustomerCode());
      }
      qal.setCustomerName(customer.getName());
    } else if (!qal.getCustomerUser().getId().equals(customer.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bukan Customer terkait");
    }
    qal.setStatus(QalStatus.APPROVED);
    qal.setApprovedAt(Instant.now());
    return toDto(qalRepo.save(qal));
  }

  @GetMapping("/spk")
  public List<SpkDto> spkList(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireAdmin(authHeader);
    return spkRepo.findAll().stream()
        .map(s -> new SpkDto(s.getId(), s.getSpkNumber(), s.getJobName()))
        .toList();
  }

  @PostMapping("/spk")
  public SpkDto createSpk(@RequestHeader("Authorization") String authHeader,
                          @RequestBody SpkDto req) {
    authHelper.requireAdmin(authHeader);
    if (req.spkNumber() == null || req.spkNumber().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SPK wajib diisi");
    if (req.jobName() == null || req.jobName().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama pekerjaan wajib diisi");
    Spk spk = spkRepo.findBySpkNumber(req.spkNumber().trim()).orElse(null);
    if (spk == null) {
      spk = new Spk();
    }
    spk.setSpkNumber(req.spkNumber().trim());
    spk.setJobName(req.jobName().trim());
    spk = spkRepo.save(spk);
    return new SpkDto(spk.getId(), spk.getSpkNumber(), spk.getJobName());
  }

  @GetMapping("/users/{role}")
  public List<UserLiteDto> usersByRole(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable String role) {
    authHelper.requireAdmin(authHeader);
    Role r;
    try {
      r = Role.valueOf(role.toUpperCase());
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role tidak valid");
    }
    return userRepo.findByRole(r).stream()
        .map(u -> new UserLiteDto(u.getId(), u.getName(), u.getEmail(), u.getRole().name()))
        .toList();
  }

  @PostMapping("/users/driver")
  public UserLiteDto createDriverUser(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody UserCreateRequest req) {
    authHelper.requireAdmin(authHeader);
    if (req.name() == null || req.name().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama wajib diisi");
    if (req.email() == null || req.email().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email wajib diisi");
    if (req.password() == null || req.password().length() < 6)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password minimal 6 karakter");
    if (userRepo.existsByEmail(req.email().trim()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah digunakan");
    User user = new User();
    user.setName(req.name().trim());
    user.setEmail(req.email().trim());
    user.setPhone(req.phone() == null ? null : req.phone().trim());
    user.setRole(Role.DRIVER);
    user.setPasswordHash(passwordEncoder.encode(req.password()));
    userRepo.save(user);
    return new UserLiteDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
  }

  @GetMapping("/masters/admin")
  public List<AdminProfileDto> adminProfiles(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireAdmin(authHeader);
    return adminProfileRepo.findAll().stream()
        .map(p -> new AdminProfileDto(p.getId(), p.getUser().getId(), p.getUser().getName(),
            p.getUser().getEmail(), p.getAdminCode(), p.getPosition()))
        .toList();
  }

  @PostMapping("/masters/admin")
  public AdminProfileDto saveAdminProfile(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody AdminProfileDto req) {
    authHelper.requireAdmin(authHeader);
    if (req.userId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Admin wajib dipilih");
    if (req.adminCode() == null || req.adminCode().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Admin wajib diisi");
    if (req.position() == null || req.position().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jabatan Admin wajib diisi");
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    AdminProfile profile = adminProfileRepo.findByUserId(user.getId()).orElse(new AdminProfile());
    profile.setUser(user);
    profile.setAdminCode(req.adminCode().trim());
    profile.setPosition(req.position().trim());
    profile = adminProfileRepo.save(profile);
    return new AdminProfileDto(profile.getId(), user.getId(), user.getName(), user.getEmail(),
        profile.getAdminCode(), profile.getPosition());
  }

  @GetMapping("/masters/customer")
  public List<CustomerProfileDto> customerProfiles(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireAdmin(authHeader);
    return customerProfileRepo.findAll().stream()
        .map(p -> new CustomerProfileDto(p.getId(), p.getUser().getId(), p.getUser().getName(),
            p.getUser().getEmail(), p.getCustomerCode()))
        .toList();
  }

  @PostMapping("/masters/customer")
  public CustomerProfileDto saveCustomerProfile(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody CustomerProfileDto req) {
    authHelper.requireAdmin(authHeader);
    if (req.userId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Customer wajib dipilih");
    if (req.customerCode() == null || req.customerCode().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Customer wajib diisi");
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    CustomerProfile profile = customerProfileRepo.findByUserId(user.getId()).orElse(new CustomerProfile());
    profile.setUser(user);
    profile.setCustomerCode(req.customerCode().trim());
    profile = customerProfileRepo.save(profile);
    return new CustomerProfileDto(profile.getId(), user.getId(), user.getName(), user.getEmail(), profile.getCustomerCode());
  }

  @GetMapping("/masters/driver")
  public List<DriverProfileDto> driverProfiles(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireAdmin(authHeader);
    return driverProfileRepo.findAll().stream()
        .map(p -> new DriverProfileDto(p.getId(), p.getUser().getId(), p.getUser().getName(),
            p.getUser().getEmail(), p.getDriverCode()))
        .toList();
  }

  @PostMapping("/masters/driver")
  public DriverProfileDto saveDriverProfile(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody DriverProfileDto req) {
    authHelper.requireAdmin(authHeader);
    if (req.userId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Driver wajib dipilih");
    if (req.driverCode() == null || req.driverCode().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Driver wajib diisi");
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    DriverProfile profile = driverProfileRepo.findByUserId(user.getId()).orElse(new DriverProfile());
    profile.setUser(user);
    profile.setDriverCode(req.driverCode().trim());
    profile = driverProfileRepo.save(profile);
    return new DriverProfileDto(profile.getId(), user.getId(), user.getName(), user.getEmail(), profile.getDriverCode());
  }

  private QalDto toDto(QalRecord qal) {
    List<QalDetailDto> details = detailRepo.findByQalQalNumberOrderByIdAsc(qal.getQalNumber())
        .stream().map(d -> new QalDetailDto(
            d.getId(),
            d.getDocumentName(),
            d.getDocumentType(),
            d.getReceivedDate(),
            d.getVerificationStatus()
        )).toList();
    return new QalDto(
        qal.getQalNumber(),
        qal.getQalNumber(),
        qal.getQalDate(),
        qal.getSpk().getSpkNumber(),
        qal.getSpk().getJobName(),
        qal.getAdminCode(),
        nameOf(qal.getAdminUser()),
        qal.getAdminPosition(),
        qal.getDriverCode(),
        qal.getDriverName() != null ? qal.getDriverName() : nameOf(qal.getDriverUser()),
        qal.getCustomerCode(),
        qal.getCustomerName() != null ? qal.getCustomerName() : nameOf(qal.getCustomerUser()),
        qal.getStatus().name(),
        details
    );
  }

  private boolean canAccess(User user, QalRecord qal) {
    if (user.getRole() == Role.ADMIN) return true;
    if (user.getRole() == Role.DRIVER && qal.getDriverUser() != null)
      return qal.getDriverUser().getId().equals(user.getId());
    if (user.getRole() == Role.CUSTOMER && qal.getCustomerUser() != null)
      return qal.getCustomerUser().getId().equals(user.getId());
    return false;
  }

  private static String nameOf(User user) {
    return user == null ? null : user.getName();
  }

  private static String safe(String v, int max, String fallback) {
    if (v == null) return fallback;
    String t = v.trim();
    if (t.isEmpty()) return fallback;
    return t.length() > max ? t.substring(0, max) : t;
  }
}
