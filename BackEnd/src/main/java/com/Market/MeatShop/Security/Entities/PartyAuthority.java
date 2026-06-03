package com.Market.MeatShop.Security.Entities;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "party_authorities")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyAuthority {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(
      name = "party_authorities_seq_gen",
      sequenceName = "party_authorities_seq",
      allocationSize = 50)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "authority_id", nullable = false)
  private Authority authority;

  @Column(name = "party_type", nullable = false, length = 100)
  @Enumerated(EnumType.STRING)
  private SecuritySubjectType partyType;

  @Column(name = "party_id", nullable = false)
  private Long partyId;

  @Column(nullable = false, name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
