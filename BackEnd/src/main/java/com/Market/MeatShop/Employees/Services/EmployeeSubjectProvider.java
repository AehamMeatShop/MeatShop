package com.Market.MeatShop.Employees.Services;

import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Repositories.EmployeeRepo;
import com.Market.MeatShop.Security.Assemblers.SecurityIdentity;
import com.Market.MeatShop.Security.Assemblers.SecuritySubject;
import com.Market.MeatShop.Security.Assemblers.SecuritySubjectProvider;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmployeeSubjectProvider implements SecuritySubjectProvider {

  private final EmployeeRepo employeeRepo;

  public EmployeeSubjectProvider(EmployeeRepo employeeRepo) {
    this.employeeRepo = employeeRepo;
  }

  @Override
  public SecuritySubjectType supports() {
    return SecuritySubjectType.EMPLOYEE;
  }

  @Override
  public SecurityIdentity getSubject(Long id) {

    Employee emp =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));

    SecurityIdentity empIdentity =
        new SecurityIdentity(emp.getId(), supports(), emp.getEmail(), emp.getPassword());
    return empIdentity;
  }
}
