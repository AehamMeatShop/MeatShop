package com.Market.MeatShop.Security.Entities;

import com.Market.MeatShop.Security.Enums.SessionState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "Sessions")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Session {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = "sessions_seq_gen", sequenceName = "sessions_seq", allocationSize = 50)
  private Long id;

  @Column(name = "refresh_token_hash", nullable = false)
  private String refreshToken;

  @Column(nullable = false, name = "party_type")
  private String partyType;

  @Column(nullable = false, name = "party_id")
  private Long partyId;

  @Column(nullable = false, name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SessionState state;

  @Column(
      nullable = false,
      name = "baseline_fingerprint") // ip/Did/screanResolution/OS/OSVersion/Browser
  private String baseLineFingerPrint;

  @Column(nullable = false, name = "last_fingerprint")
  private String lastFingerprint;

  @Column(nullable = false, name = "trust_score")
  @Min(value = 0)
  @Max(value = 100)
  private Integer trustScore;
}
