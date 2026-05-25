package com.Market.MeatShop.Employees.Services;

import com.Market.MeatShop.Employees.DTOs.EmployeeDetails;
import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Repositories.EmployeeRepo;
import com.Market.MeatShop.Security.Services.RoleService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDetailsService implements UserDetailsService {
  private final EmployeeRepo empRepo;
  private final RoleService roleService;

  public EmployeeDetailsService(EmployeeRepo empRepo, RoleService roleService) {
    this.empRepo = empRepo;
    this.roleService = roleService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Employee emp =
        empRepo
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    var roleViewDto = roleService.getRoleById(emp.getRoleId());
    return new EmployeeDetails(emp, roleViewDto.name());
  }
}
