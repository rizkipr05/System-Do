package com.doapp.qal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "qal_details")
public class QalDetail {
  @Id
  @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "qal_number", nullable = false, referencedColumnName = "qal_number")
  private QalRecord qal;

  @Column(name = "document_name", nullable = false, length = 200)
  private String documentName;

  @Column(name = "document_type", nullable = false, length = 80)
  private String documentType;

  @Column(name = "received_date", nullable = false)
  private LocalDate receivedDate;

  @Column(name = "verification_status", nullable = false, length = 40)
  private String verificationStatus;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public QalRecord getQal() { return qal; }
  public void setQal(QalRecord qal) { this.qal = qal; }

  public String getDocumentName() { return documentName; }
  public void setDocumentName(String documentName) { this.documentName = documentName; }

  public String getDocumentType() { return documentType; }
  public void setDocumentType(String documentType) { this.documentType = documentType; }

  public LocalDate getReceivedDate() { return receivedDate; }
  public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

  public String getVerificationStatus() { return verificationStatus; }
  public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}
