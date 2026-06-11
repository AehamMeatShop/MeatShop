package com.Market.MeatShop.Employees.Services;

import com.Market.MeatShop.Employees.DTOs.EmployeeFullViewDTO;
import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;
import com.Market.MeatShop.Employees.DTOs.Requests.*;
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
  private final PartyService partyService;
  private final PartyContactService partyContactService;
  private final PasswordEncoder encoder;
  private final CompromisedPasswordChecker dPc;
  private final RoleService roleService;
  private final LoginIndexService loginIndexService;
  private final AuthorityService authorityService;
  private final AuthService authService;

  public EmployeeService(
      EmployeeRepo employeeRepo,
      EmployeeMapper employeeMapper,
      PartyService partyService,
      PartyContactService partyContactService,
      PasswordEncoder encoder,
      CompromisedPasswordChecker dPc,
      RoleService roleService,
      LoginIndexService loginIndexService,
      AuthorityService authorityService,
      AuthService authService) {
    this.employeeRepo = employeeRepo;
    this.employeeMapper = employeeMapper;
    this.partyService = partyService;
    this.partyContactService = partyContactService;
    this.encoder = encoder;
    this.dPc = dPc;
    this.roleService = roleService;
    this.loginIndexService = loginIndexService;
    this.authorityService = authorityService;
    this.authService = authService;
  }

  @Transactional
  public EmployeeViewDTO createEmployee(CreateEmployeeReq req) {
    log.info("Attempting to create employee with email: {}", req.email());

    CreatePartyRequest partyReq =
        new CreatePartyRequest(req.name(), req.address(), PartyType.EMPLOYEE);

    CompromisedPasswordDecision decision = dPc.check(req.password());
    if (decision.isCompromised()) {
      throw new PasswordCompromisedException("password is compromised");
    }

    Long partyId = partyService.createParty(partyReq).id();
    log.info("Party created with id: {}", partyId);

    Employee emp = new Employee();
    emp.setEmail(req.email());
    emp.setPassword(encoder.encode(req.password()));
    emp.setSalary(req.salary());
    emp.setStatus(req.status());
    emp.setPartyId(partyId);
    employeeRepo.save(emp);
    log.info("Employee saved with id: {}", emp.getId());

    boolean indexCreated =
        loginIndexService.createIndex(emp.getId(), SecuritySubjectType.EMPLOYEE, req.email());
    log.info("Login index creation result: {} for email: {}", indexCreated, req.email());

    if (!indexCreated) {
      log.error(
          "Failed to create login index for email: {}, rolling back employee creation",
          req.email());
      throw new RuntimeException("Failed to create login index");
    }

    EmployeeViewDTO resp = employeeMapper.toEmployeeViewDTO(emp);
    log.info("employee created successfully {}", resp);
    return resp;
  }

  public PartyContactViewDTO createEmployeeContact(CreateEmpContactReq req) {
    Employee emp =
        employeeRepo
            .findById(req.employeeId())
            .orElseThrow(() -> new TargetNotFound("employee not found"));
    CreatePartyContactReq partyContactReq =
        new CreatePartyContactReq(emp.getPartyId(), req.method(), req.identifier());
    PartyContactViewDTO resp = partyContactService.createPartyContact(partyContactReq);
    log.info("employee contact created {}", resp);
    return resp;
  }

  @Transactional
  public EmployeeFullViewDTO updateEmployee(UpdateEmployeeProfileReq req, Long id) {
    log.info("Attempting to update employee with id: {}", id);

    Employee emp =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));

    String oldEmail = emp.getEmail();
    String newEmail = req.email();

    UpdatePartyResp partyResp =
        partyService.updateParty(
            new UpdatePartyReq(req.name(), req.address(), null), emp.getPartyId());

    Long roleId = null;

    UpdateEmployeeReq empUpdReq =
        new UpdateEmployeeReq(req.email(), req.password(), req.salary(), req.status());

    if (partyResp.updated()) {
      emp = employeeMapper.updateFromReq(empUpdReq, emp);
      employeeRepo.save(emp);
    } else {
      Employee originalCopy = employeeMapper.clone(emp);
      emp = employeeMapper.updateFromReq(empUpdReq, emp);

      if (EmployeeComparison.hasNoChanges(originalCopy, emp, empUpdReq)) {
        throw new IllegalArgumentException("no changes");
      }

      employeeRepo.save(emp);
    }

    log.info("Employee updated with id: {}", emp.getId());

    if (newEmail != null && !newEmail.equals(oldEmail)) {
      log.info("Email changed from {} to {}, updating login index", oldEmail, newEmail);
      boolean indexUpdated = loginIndexService.updateEmail(oldEmail, newEmail);
      log.info(
          "Login index update result: {} for email change from {} to {}",
          indexUpdated,
          oldEmail,
          newEmail);

      if (!indexUpdated) {
        log.error(
            "Failed to update login index for email change from {} to {}, rolling back employee update",
            oldEmail,
            newEmail);
        throw new RuntimeException("Failed to update login index");
      }
    }

    EmployeeFullViewDTO resp =
        new EmployeeFullViewDTO(employeeMapper.toEmployeeViewDTO(emp), partyResp.partyInfo());
    log.info("employee updated successfully {}", resp);
    return resp;
  }

  @Transactional
  public void deleteEmployee(Long id) {
    log.info("Attempting to delete employee with id: {}", id);

    Employee employee =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));

    log.info("Checking if employee has SUPER_ADMIN role");
    boolean isSuperAdmin =
        roleService.partyHasSuperAdminRole(SecuritySubjectType.EMPLOYEE, employee.getId());

    if (isSuperAdmin) {
      log.error("Cannot delete employee with SUPER_ADMIN role");
      throw new IllegalArgumentException("Cannot delete employee with SUPER_ADMIN role");
    }

    String email = employee.getEmail();
    log.info("Deleting login index for email: {}", email);
    boolean indexDeleted = loginIndexService.deleteIndex(email);
    log.info("Login index deletion result: {} for email: {}", indexDeleted, email);

    if (!indexDeleted) {
      log.error(
          "Failed to delete login index for email: {}, rolling back employee deletion", email);
      throw new TargetNotFound("Failed to delete login index");
    }

    log.info("Deleting party roles for employee id: {}", employee.getId());
    roleService.removeAllRolesForParty(SecuritySubjectType.EMPLOYEE, employee.getId());

    log.info("Deleting party authorities for employee id: {}", employee.getId());
    authorityService.removeAllAuthoritiesForParty(SecuritySubjectType.EMPLOYEE, employee.getId());

    partyService.deleteParty(employee.getPartyId());
    employeeRepo.delete(employee);
    log.info("employee deleted successfully {}", id);
  }

  public EmployeeFullViewDTO getEmployeeById(Long id) {
    Employee employee =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));
    PartyViewDTO partyViewDTO = partyService.findPartyById(employee.getPartyId());
    return new EmployeeFullViewDTO(employeeMapper.toEmployeeViewDTO(employee), partyViewDTO);
  }

  public Page<EmployeeFullViewDTO> getEmployeesByFilter(
      EmployeeFilterReq filter, Pageable pageable) {
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

    List<Long> partyIds = content.stream().map(Employee::getPartyId).distinct().toList();
    PartyFilterReq partyFilterReq =
        new PartyFilterReq(
            null, filter.name(), filter.address(), PartyType.EMPLOYEE, null, null, null, null);
    List<PartyViewDTO> parties = partyService.findByFilterServ(partyFilterReq, partyIds);
    Map<Long, PartyViewDTO> partyMap =
        parties.stream().collect(Collectors.toMap(PartyViewDTO::id, p -> p));

    List<EmployeeFullViewDTO> result =
        content.stream()
            .map(
                emp -> {
                  PartyViewDTO party = partyMap.get(emp.getPartyId());

                  if (party == null) return null;

                  return new EmployeeFullViewDTO(employeeMapper.toEmployeeViewDTO(emp), party);
                })
            .filter(Objects::nonNull)
            .toList();

    Page<EmployeeFullViewDTO> resultPage =
        new PageImpl<>(result, employeesPage.getPageable(), employeesPage.getTotalElements());
    log.info("employees returned {}", resultPage.getContent());
    return resultPage;
  }

  @Transactional
  public EmployeeViewDTO startApplication(CreateEmployeeReq req) {
    log.info("Attempting to sart application by create employee with email: {}", req.email());

    CreatePartyRequest partyReq =
        new CreatePartyRequest(req.name(), req.address(), PartyType.EMPLOYEE);

    CompromisedPasswordDecision decision = dPc.check(req.password());
    if (decision.isCompromised()) {
      throw new PasswordCompromisedException("password is compromised");
    }

    Long partyId = partyService.createParty(partyReq).id();
    log.info("Party created with id: {}", partyId);

    Employee emp = new Employee();
    emp.setEmail(req.email());
    emp.setPassword(encoder.encode(req.password()));
    emp.setSalary(req.salary());
    emp.setStatus(req.status());
    emp.setPartyId(partyId);
    employeeRepo.save(emp);
    log.info("Employee saved with id: {}", emp.getId());

    boolean indexCreated =
        loginIndexService.createIndex(emp.getId(), SecuritySubjectType.EMPLOYEE, req.email());
    log.info("Login index creation result: {} for email: {}", indexCreated, req.email());

    if (!indexCreated) {
      log.error(
          "Failed to create login index for email: {}, rolling back employee creation",
          req.email());
      throw new RuntimeException("Failed to create login index");
    }
    authService.startSecurityApplication();
    RoleViewDto superAdminRole = roleService.getRoleByName("SUPER_ADMIN");
    if (superAdminRole == null) {
      throw new TargetNotFound("SUPER_ADMIN role not found cannot start application");
    }
    roleService.assignRoleToParty(
        new AssignRoleToPartyRequest(
            SecuritySubjectType.EMPLOYEE, emp.getId(), superAdminRole.id()));

    List<String> systemAuthorites =
        Arrays.stream(SystemAuthorities.values()).map(SystemAuthorities::name).toList();

    for (String authority : systemAuthorites) {
      authorityService.createAuthority(new CreateAuthorityRequest(authority));
    }

    EmployeeViewDTO resp = employeeMapper.toEmployeeViewDTO(emp);
    log.info("employee created successfully and application started succesfolly  {}", resp);
    return resp;
  }
}
