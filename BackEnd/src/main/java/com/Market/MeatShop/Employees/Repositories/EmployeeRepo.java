package com.Market.MeatShop.Employees.Repositories;

import com.Market.MeatShop.Employees.Entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo
    extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
  Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

  Optional<Employee> findByEmail(String email);
}
