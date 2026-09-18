package com.Market.MeatShop.Employees.Services;

import com.Market.MeatShop.Employees.DTOs.EmployeeFullViewDTO;
import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;
import com.Market.MeatShop.Employees.DTOs.Requests.*;
import com.Market.MeatShop.Employees.DTOs.Rsponses.EmployeeUpdateResponse;
import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Mappers.EmployeeMapper;
import com.Market.MeatShop.Employees.QueryRoles.EmployeeQueryRoles;
import com.Market.MeatShop.Employees.Repositories.EmployeeRepo;
import com.Market.MeatShop.Employees.Specifications.EmployeeSpecification;
import com.Market.MeatShop.Employees.Utils.EmployeeComparison;
import com.Market.MeatShop.Parties.DTOs.PartyContactViewDTO;
import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.PartyFilterReq;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.DTOs.Responses.UpdatePartyResp;
import com.Market.MeatShop.Parties.Enums.PartyType;
import com.Market.MeatShop.Parties.QueryRoles.PartyQueryRoles;
import com.Market.MeatShop.Parties.Services.PartyContactService;
import com.Market.MeatShop.Parties.Services.PartyService;

import com.Market.MeatShop.Security.DTOs.Requests.AssignRoleToPartyRequest;
import com.Market.MeatShop.Security.DTOs.Requests.CreateAuthorityRequest;
import com.Market.MeatShop.Security.DTOs.RoleViewDto;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;

import com.Market.MeatShop.Security.Services.AuthService;
import com.Market.MeatShop.Security.Services.AuthorityService;
import com.Market.MeatShop.Security.Services.LoginIndexService;
import com.Market.MeatShop.Security.Services.RoleService;
import com.Market.MeatShop.Shared.Exceptions.PasswordCompromisedException;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import com.Market.MeatShop.Utils.SystemAuthorities;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeService {
  private final EmployeeMapper employeeMapper;
  private final EmployeeRepo employeeRepo;

  private final PasswordEncoder encoder;
  private final CompromisedPasswordChecker dPc;

  public EmployeeService(
      EmployeeRepo employeeRepo,
      EmployeeMapper employeeMapper,
      PartyService partyService,
      PasswordEncoder encoder,
      CompromisedPasswordChecker dPc) {
    this.employeeRepo = employeeRepo;
    this.employeeMapper = employeeMapper;

    this.encoder = encoder;
    this.dPc = dPc;
  }

  // completed happy scenario
  @Transactional
  public EmployeeViewDTO createEmployee(CreateEmployeeReq req) {

    CompromisedPasswordDecision decision = dPc.check(req.password());
    if (decision.isCompromised()) {
      throw new PasswordCompromisedException("password is compromised");
    }

    Employee emp = new Employee();
    emp.setEmail(req.email());
    emp.setPassword(encoder.encode(req.password()));
    emp.setSalary(req.salary());
    emp.setStatus(req.status());
    emp.setPartyId(req.partyId());
    employeeRepo.save(emp);
    log.info("Employee saved with id: {}", emp.getId());

    EmployeeViewDTO resp = employeeMapper.toEmployeeViewDTO(emp);
    log.info("employee created successfully {}", resp);
    return resp;
  }

  // completed happy scenario

  @Transactional
  public EmployeeUpdateResponse updateEmployee(UpdateEmployeeReq req, Long id) {
    log.info("Attempting to update employee with id: {}", id);

    Employee emp =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));
    EmployeeViewDTO oldData = employeeMapper.toEmployeeViewDTO(emp);
    emp = employeeMapper.updateFromReq(req, emp);
    if (req.password() != null || !req.password().isEmpty()) {

      CompromisedPasswordDecision decision = dPc.check(req.password());
      if (decision.isCompromised()) {
        throw new PasswordCompromisedException("password is compromised");
      }
      emp.setPassword(encoder.encode(req.password()));
    }

    employeeRepo.save(emp);
    EmployeeViewDTO newData = employeeMapper.toEmployeeViewDTO(emp);

    log.info("employee updated successfully {}", newData);
    return new EmployeeUpdateResponse(oldData, newData);
  }

  @Transactional
  public EmployeeViewDTO deleteEmployee(Long id) {
    log.info("Attempting to delete employee with id: {}", id);

    Employee employee =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));
    EmployeeViewDTO deletedProfile = employeeMapper.toEmployeeViewDTO(employee);

    employeeRepo.delete(employee);
    log.info("employee deleted successfully {}", id);
    return deletedProfile;
  }

  public EmployeeViewDTO getEmployeeById(Long id) {
    Employee employee =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));

    return employeeMapper.toEmployeeViewDTO(employee);
  }

  public Page<EmployeeViewDTO> getEmployeesByFilter(EmployeeFilterReq filter, Pageable pageable) {
    pageable
        .getSort()
        .forEach(
            sort -> {
              if (!EmployeeQueryRoles.ALLOWED_SORT_FIELDS.contains(sort.getProperty())) {
                throw new IllegalArgumentException("Sorting not allowed on: " + sort.getProperty());
              }
            });
    if (pageable.getPageSize() > PartyQueryRoles.maxPageSize) {
      throw new IllegalArgumentException(
          "Page size is greater than " + EmployeeQueryRoles.maxPageSize);
    }
    Specification<Employee> spec = Specification.allOf();
    if (filter.email() != null) {
      spec = spec.and(EmployeeSpecification.asEmail(filter.email()));
    }

    if (filter.status() != null) {
      spec = spec.and(EmployeeSpecification.asStatus(filter.status()));
    }
    if (filter.maxSalary() != null || filter.minSalary() != null) {
      spec =
          spec.and(EmployeeSpecification.betweenSalaries(filter.minSalary(), filter.maxSalary()));
    }
    if (filter.fromCreatedAt() != null || filter.toCreatedAt() != null) {
      spec =
          spec.and(
              EmployeeSpecification.betweenCreatingDates(
                  filter.fromCreatedAt(), filter.toCreatedAt()));
    }
    if (filter.fromUpdatedAt() != null || filter.toUpdatedAt() != null) {
      spec =
          spec.and(
              EmployeeSpecification.betweenUpdatingDates(
                  filter.fromUpdatedAt(), filter.toUpdatedAt()));
    }

    Page<Employee> employeesPage = employeeRepo.findAll(spec, pageable);
    List<Employee> content = employeesPage.getContent();
    Page<EmployeeViewDTO> resp = employeesPage.map(employeeMapper::toEmployeeViewDTO);
    return resp;
  }
}
