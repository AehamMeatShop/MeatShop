package com.Market.MeatShop.Employees.Entities;

import com.Market.MeatShop.Employees.Enums.EmployeeRole;
import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import com.Market.MeatShop.Parties.Entities.Party;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Table(name= "employees")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "employees_seq_gen",
            sequenceName = "employees_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "salary" , nullable = false)
    @DecimalMin(value = "0" )
    private long salary;

    @OneToOne
    @JoinColumn(name = "party_id" , nullable = false)
    private Party party;

    @CreationTimestamp
    @Column( name = "created_at",nullable = false ,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column( name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

     @Column(name = "status" , nullable = false)
     @Enumerated(EnumType.STRING)
     private EmployeeStatus status;

}
