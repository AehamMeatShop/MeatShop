package com.Market.MeatShop.Security.Entities;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "party_roles")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyRole {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(
      name = "party_roles_seq_gen",
      sequenceName = "party_roles_seq",
      allocationSize = 50)
  private Long id;

  @Column(name = "party_id", nullable = false)
  private Long partyId;

  @Column(name = "party_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private SecuritySubjectType partyType;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;
}
