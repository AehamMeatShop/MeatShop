package com.Market.MeatShop.Employees.Entities;

import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import com.Market.MeatShop.Parties.Entities.Party;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

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
            sequenceName = "employees_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "email" ,unique = true , nullable = false , length = 100 )
    private String email;
    @Column( name = "password",length = 100 , nullable = false  )
    @Length(max = 100 , min = 8)
    private String password;


    @Column(name = "salary" , nullable = false)
    @DecimalMin(value = "0" )
    private long salary;


    @Column(name = "party_id" , nullable = false)
    private Long partyId;

    @CreationTimestamp
    @Column( name = "created_at",nullable = false ,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column( name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

     @Column(name = "status" , nullable = false)
     @Enumerated(EnumType.STRING)
     private EmployeeStatus status;

}
