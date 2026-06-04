package com.Market.MeatShop.Security.Entities;

import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "login_indexes")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginIndex {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(
      name = "login_indexes_seq_gen",
      sequenceName = "login_indexes_seq",
      allocationSize = 50)
  private Long id;

  @Column(name = "subject_id", nullable = false)
  private Long subjectId;

  @Column(name = "subject_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private SecuritySubjectType subjectType;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(nullable = false, name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
