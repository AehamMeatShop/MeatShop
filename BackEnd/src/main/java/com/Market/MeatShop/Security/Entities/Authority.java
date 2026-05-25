package com.Market.MeatShop.Security.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionIdMutability;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "authorities")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authority {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(
      name = "authorities_seq_gen",
      sequenceName = "authorities_seq",
      allocationSize = 50)
  private Long id;

  @Column(name = "authority", nullable = false, unique = true, updatable = false)
  String authority;

  @Column(nullable = false, name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
