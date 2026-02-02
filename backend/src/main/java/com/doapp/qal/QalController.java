package com.doapp.qal;

import com.doapp.auth.AuthHelper;
import com.doapp.qal.dto.QalCreateRequest;
import com.doapp.qal.dto.QalDetailDto;
import com.doapp.qal.dto.QalDto;
import com.doapp.qal.dto.OwnerProfileDto;
import com.doapp.qal.dto.ProjectControlProfileDto;
import com.doapp.qal.dto.QcProfileDto;
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
  private final QcProfileRepository qcProfileRepo;
  private final OwnerProfileRepository ownerProfileRepo;
  private final ProjectControlProfileRepository pcProfileRepo;
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;

  public QalController(AuthHelper authHelper,
                       QalRepository qalRepo,
                       QalDetailRepository detailRepo,
                       SpkRepository spkRepo,
                       QcProfileRepository qcProfileRepo,
                       OwnerProfileRepository ownerProfileRepo,
                       ProjectControlProfileRepository pcProfileRepo,
                       UserRepository userRepo,
                       PasswordEncoder passwordEncoder) {
    this.authHelper = authHelper;
    this.qalRepo = qalRepo;
    this.detailRepo = detailRepo;
    this.spkRepo = spkRepo;
    this.qcProfileRepo = qcProfileRepo;
    this.ownerProfileRepo = ownerProfileRepo;
    this.pcProfileRepo = pcProfileRepo;
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public List<QalDto> list(@RequestHeader("Authorization") String authHeader) {
    User user = authHelper.requireUser(authHeader);
    if (user.getRole() == Role.QUALITY_CONTROL) {
      return qalRepo.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }
    if (user.getRole() == Role.PROJECT_CONTROL) {
      return qalRepo.findByProjectControlUserIdOrderByCreatedAtDesc(user.getId())
          .stream().map(this::toDto).toList();
    }
    if (user.getRole() == Role.OWNER) {
      return qalRepo.findByOwnerUserIdOrderByCreatedAtDesc(user.getId())
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
    User qc = authHelper.requireQualityControl(authHeader);
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

    QcProfile qcProfile = qcProfileRepo.findByUserId(qc.getId()).orElse(null);
    if (qcProfile == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profil QC belum diisi");
    }

    User pc = null;
    ProjectControlProfile pcProfile = null;
    if (req.projectControlUserId() != null) {
      pc = userRepo.findById(req.projectControlUserId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project Control tidak ditemukan"));
      pcProfile = pcProfileRepo.findByUserId(pc.getId()).orElse(null);
      if (pcProfile == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profil Project Control belum diisi");
      }
    }
    User owner = null;
    if (req.ownerUserId() != null) {
      owner = userRepo.findById(req.ownerUserId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner tidak ditemukan"));
    }
    OwnerProfile ownerProfile = null;
    if (owner != null) {
      ownerProfile = ownerProfileRepo.findByUserId(owner.getId()).orElse(null);
      if (ownerProfile == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profil Owner belum diisi");
      }
    }

    QalRecord qal = new QalRecord();
    qal.setQalNumber(qalNumber);
    qal.setQalDate(req.qalDate());
    qal.setSpk(spk);
    qal.setQcUser(qc);
    qal.setQcCode(qcProfile.getQcCode());
    qal.setQcPosition(qcProfile.getPosition());
    qal.setProjectControlUser(pc);
    qal.setProjectControlCode(pcProfile == null ? null : pcProfile.getPcCode());
    qal.setProjectControlName(pc == null ? null : pc.getName());
    qal.setOwnerUser(owner);
    qal.setOwnerCode(ownerProfile == null ? null : ownerProfile.getOwnerCode());
    qal.setOwnerName(owner == null ? null : owner.getName());
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
    User pc = authHelper.requireProjectControl(authHeader);
    QalRecord qal = qalRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QAL tidak ditemukan"));
    if (qal.getStatus() != QalStatus.DRAFT)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status tidak valid untuk TTD");
    if (qal.getProjectControlUser() == null) {
      qal.setProjectControlUser(pc);
      ProjectControlProfile pcProfile = pcProfileRepo.findByUserId(pc.getId()).orElse(null);
      if (pcProfile != null) {
        qal.setProjectControlCode(pcProfile.getPcCode());
      }
      qal.setProjectControlName(pc.getName());
    } else if (!qal.getProjectControlUser().getId().equals(pc.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bukan Project Control terkait");
    }
    qal.setStatus(QalStatus.SIGNED);
    qal.setSignedAt(Instant.now());
    return toDto(qalRepo.save(qal));
  }

  @PostMapping("/{id}/approve")
  public QalDto approve(@RequestHeader("Authorization") String authHeader,
                        @PathVariable String id) {
    User owner = authHelper.requireOwner(authHeader).getUser();
    QalRecord qal = qalRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QAL tidak ditemukan"));
    if (qal.getStatus() != QalStatus.SIGNED)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status tidak valid untuk ACC");
    if (qal.getOwnerUser() == null) {
      qal.setOwnerUser(owner);
      OwnerProfile ownerProfile = ownerProfileRepo.findByUserId(owner.getId()).orElse(null);
      if (ownerProfile != null) {
        qal.setOwnerCode(ownerProfile.getOwnerCode());
      }
      qal.setOwnerName(owner.getName());
    } else if (!qal.getOwnerUser().getId().equals(owner.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bukan Owner terkait");
    }
    qal.setStatus(QalStatus.APPROVED);
    qal.setApprovedAt(Instant.now());
    return toDto(qalRepo.save(qal));
  }

  @GetMapping("/spk")
  public List<SpkDto> spkList(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return spkRepo.findAll().stream()
        .map(s -> new SpkDto(s.getId(), s.getSpkNumber(), s.getJobName()))
        .toList();
  }

  @PostMapping("/spk")
  public SpkDto createSpk(@RequestHeader("Authorization") String authHeader,
                          @RequestBody SpkDto req) {
    authHelper.requireQualityControl(authHeader);
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
    authHelper.requireQualityControl(authHeader);
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

  @PostMapping("/users/project-control")
  public UserLiteDto createProjectControlUser(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody UserCreateRequest req) {
    authHelper.requireQualityControl(authHeader);
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
    user.setRole(Role.PROJECT_CONTROL);
    user.setPasswordHash(passwordEncoder.encode(req.password()));
    userRepo.save(user);
    return new UserLiteDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
  }

  @GetMapping("/masters/qc")
  public List<QcProfileDto> qcProfiles(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return qcProfileRepo.findAll().stream()
        .map(p -> new QcProfileDto(p.getId(), p.getUser().getId(), p.getUser().getName(),
            p.getUser().getEmail(), p.getQcCode(), p.getPosition()))
        .toList();
  }

  @PostMapping("/masters/qc")
  public QcProfileDto saveQcProfile(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody QcProfileDto req) {
    authHelper.requireQualityControl(authHeader);
    if (req.userId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User QC wajib dipilih");
    if (req.qcCode() == null || req.qcCode().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID QC wajib diisi");
    if (req.position() == null || req.position().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jabatan QC wajib diisi");
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    QcProfile profile = qcProfileRepo.findByUserId(user.getId()).orElse(new QcProfile());
    profile.setUser(user);
    profile.setQcCode(req.qcCode().trim());
    profile.setPosition(req.position().trim());
    profile = qcProfileRepo.save(profile);
    return new QcProfileDto(profile.getId(), user.getId(), user.getName(), user.getEmail(),
        profile.getQcCode(), profile.getPosition());
  }

  @GetMapping("/masters/owner")
  public List<OwnerProfileDto> ownerProfiles(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return ownerProfileRepo.findAll().stream()
        .map(p -> new OwnerProfileDto(p.getId(), p.getUser().getId(), p.getUser().getName(),
            p.getUser().getEmail(), p.getOwnerCode()))
        .toList();
  }

  @PostMapping("/masters/owner")
  public OwnerProfileDto saveOwnerProfile(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody OwnerProfileDto req) {
    authHelper.requireQualityControl(authHeader);
    if (req.userId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Owner wajib dipilih");
    if (req.ownerCode() == null || req.ownerCode().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Owner wajib diisi");
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    OwnerProfile profile = ownerProfileRepo.findByUserId(user.getId()).orElse(new OwnerProfile());
    profile.setUser(user);
    profile.setOwnerCode(req.ownerCode().trim());
    profile = ownerProfileRepo.save(profile);
    return new OwnerProfileDto(profile.getId(), user.getId(), user.getName(), user.getEmail(), profile.getOwnerCode());
  }

  @GetMapping("/masters/pc")
  public List<ProjectControlProfileDto> pcProfiles(@RequestHeader("Authorization") String authHeader) {
    authHelper.requireQualityControl(authHeader);
    return pcProfileRepo.findAll().stream()
        .map(p -> new ProjectControlProfileDto(p.getId(), p.getUser().getId(), p.getUser().getName(),
            p.getUser().getEmail(), p.getPcCode()))
        .toList();
  }

  @PostMapping("/masters/pc")
  public ProjectControlProfileDto savePcProfile(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody ProjectControlProfileDto req) {
    authHelper.requireQualityControl(authHeader);
    if (req.userId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Project Control wajib dipilih");
    if (req.pcCode() == null || req.pcCode().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Project Control wajib diisi");
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    ProjectControlProfile profile = pcProfileRepo.findByUserId(user.getId()).orElse(new ProjectControlProfile());
    profile.setUser(user);
    profile.setPcCode(req.pcCode().trim());
    profile = pcProfileRepo.save(profile);
    return new ProjectControlProfileDto(profile.getId(), user.getId(), user.getName(), user.getEmail(), profile.getPcCode());
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
        qal.getQcCode(),
        nameOf(qal.getQcUser()),
        qal.getQcPosition(),
        qal.getProjectControlCode(),
        qal.getProjectControlName() != null ? qal.getProjectControlName() : nameOf(qal.getProjectControlUser()),
        qal.getOwnerCode(),
        qal.getOwnerName() != null ? qal.getOwnerName() : nameOf(qal.getOwnerUser()),
        qal.getStatus().name(),
        details
    );
  }

  private boolean canAccess(User user, QalRecord qal) {
    if (user.getRole() == Role.QUALITY_CONTROL) return true;
    if (user.getRole() == Role.PROJECT_CONTROL && qal.getProjectControlUser() != null)
      return qal.getProjectControlUser().getId().equals(user.getId());
    if (user.getRole() == Role.OWNER && qal.getOwnerUser() != null)
      return qal.getOwnerUser().getId().equals(user.getId());
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
