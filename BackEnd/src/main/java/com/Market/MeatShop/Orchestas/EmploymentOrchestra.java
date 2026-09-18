package com.Market.MeatShop.Orchestas;

import com.Market.MeatShop.Employees.DTOs.EmployeeFullViewDTO;
import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;
import com.Market.MeatShop.Employees.DTOs.Requests.*;

import com.Market.MeatShop.Employees.DTOs.Rsponses.EmployeeUpdateResponse;

import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.QueryRoles.EmployeeQueryRoles;
import com.Market.MeatShop.Employees.Services.EmployeeService;

import com.Market.MeatShop.Employees.Specifications.EmployeeSpecification;
import com.Market.MeatShop.Orchestas.requests.OrchCreateEmpReq;
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

import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import com.Market.MeatShop.Utils.SystemAuthorities;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmploymentOrchestra {
  private final EmployeeService employeeService;
  private final PartyService partyService;
  private final PartyContactService parContService;
  private final LoginIndexService loginIndexService;
  private final RoleService roleService;
  private final AuthorityService authorityService;
  private final AuthService authService;

  public EmploymentOrchestra(
      EmployeeService employeeService,
      PartyService partyService,
      PartyContactService parContService,
      LoginIndexService loginIndexService,
      RoleService roleService,
      AuthorityService authorityService,
      AuthService authService) {
    this.employeeService = employeeService;
    this.partyService = partyService;
    this.parContService = parContService;
    this.loginIndexService = loginIndexService;
    this.roleService = roleService;
    this.authorityService = authorityService;
    this.authService = authService;
  }

  public EmployeeViewDTO createEmployee(OrchCreateEmpReq req) {
    log.info("Attempting to create employee with email: {}", req.email());

    CreatePartyRequest partyReq =
        new CreatePartyRequest(req.name(), req.address(), PartyType.EMPLOYEE);

    Long partyId = partyService.createParty(partyReq).id();
    log.info("Party created with id: {}", partyId);
    EmployeeViewDTO emp =
        employeeService.createEmployee(
            new CreateEmployeeReq(
                req.email(), req.password(), req.salary(), partyId, req.status()));

    boolean indexCreated =
        loginIndexService.createIndex(emp.id(), SecuritySubjectType.EMPLOYEE, req.email());
    log.info("Login index creation result: {} for email: {}", indexCreated, req.email());

    if (!indexCreated) {
      log.error(
          "Failed to create login index for email: {}, rolling back employee creation",
          req.email());
      throw new RuntimeException("Failed to create login index");
    }

    log.info("employee created successfully {}", emp);
    return emp;
  }

  public PartyContactViewDTO createEmployeeContact(CreateEmpContactReq req) {
    EmployeeFullViewDTO emp = getEmployeeById(req.employeeId());

    CreatePartyContactReq partyContactReq =
        new CreatePartyContactReq(emp.partyInfo().id(), req.method(), req.identifier());
    PartyContactViewDTO resp = parContService.createPartyContact(partyContactReq);
    log.info("employee contact created {}", resp);
    return resp;
  }

  @Transactional
  public EmployeeFullViewDTO updateEmployee(UpdateEmployeeProfileReq req, Long id) {

    EmployeeUpdateResponse updateEmpResp =
        employeeService.updateEmployee(
            new UpdateEmployeeReq(req.email(), req.password(), req.salary(), req.status()), id);

    log.info("employee profile updated successfully {}", updateEmpResp.newData());

    UpdatePartyResp partyResp =
        partyService.updateParty(
            new UpdatePartyReq(req.name(), req.address(), null), updateEmpResp.newData().partyId());

    boolean updateIndexRes =
        loginIndexService.updateEmail(
            updateEmpResp.oldData().email(), updateEmpResp.newData().email());
    if (!updateIndexRes) {
      log.error(
          "Failed to update login index for email change from {} to {}, rolling back employee update",
          updateEmpResp.oldData().email(),
          req.email());
      throw new RuntimeException("Failed to update login index");
    }
    EmployeeFullViewDTO resp =
        new EmployeeFullViewDTO(updateEmpResp.newData(), partyResp.partyInfo());
    log.info("employee updated successfully {}", resp);
    return resp;
  }

  @Transactional
  public void deleteEmployee(Long id) {
    EmployeeViewDTO deletedProfile = employeeService.deleteEmployee(id);

    log.info("Deleting login index for email: {}", deletedProfile.email());
    boolean indexDeleted = loginIndexService.deleteIndex(deletedProfile.email());
    log.info("Login index deletion result: {} for email: {}", indexDeleted, deletedProfile.email());

    if (!indexDeleted) {
      log.error(
          "Failed to delete login index for email: {}, rolling back employee deletion",
          deletedProfile.email());
      throw new TargetNotFound("Failed to delete login index");
    }

    log.info("Deleting party roles for employee id: {}", deletedProfile.id());
    roleService.removeAllRolesForParty(SecuritySubjectType.EMPLOYEE, deletedProfile.id());

    log.info("Deleting party authorities for employee id: {}", deletedProfile.id());
    authorityService.removeAllAuthoritiesForParty(
        SecuritySubjectType.EMPLOYEE, deletedProfile.id());
    parContService.deleteAllPartyContacts(deletedProfile.partyId());
    partyService.deleteParty(deletedProfile.partyId());

    log.info("employee deleted successfully {}", id);
  }

  @Transactional
  public EmployeeViewDTO startApplication(OrchCreateEmpReq req) {
    log.info("Attempting to start application by create employee with email: {}", req.email());

    CreatePartyRequest partyReq =
        new CreatePartyRequest(req.name(), req.address(), PartyType.EMPLOYEE);

    Long partyId = partyService.createParty(partyReq).id();

    EmployeeViewDTO emp =
        employeeService.createEmployee(
            new CreateEmployeeReq(
                req.email(), req.password(), req.salary(), partyId, req.status()));
    log.info("Employee saved with id: {}", emp.id());

    boolean indexCreated =
        loginIndexService.createIndex(emp.id(), SecuritySubjectType.EMPLOYEE, emp.email());
    log.info("Login index creation result: {} for email: {}", indexCreated, emp.email());

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
        new AssignRoleToPartyRequest(SecuritySubjectType.EMPLOYEE, emp.id(), superAdminRole.id()));

    List<String> systemAuthorities =
        Arrays.stream(SystemAuthorities.values()).map(SystemAuthorities::name).toList();

    for (String authority : systemAuthorities) {
      authorityService.createAuthority(new CreateAuthorityRequest(authority));
    }

    log.info("employee created successfully and application started successfully  {}", emp);
    return emp;
  }

  public EmployeeFullViewDTO getEmployeeById(Long id) {
    EmployeeViewDTO employee = employeeService.getEmployeeById(id);

    PartyViewDTO partyViewDTO = partyService.findPartyById(employee.partyId());
    return new EmployeeFullViewDTO(employee, partyViewDTO);
  }

  public Page<EmployeeFullViewDTO> getEmployeesByFilter(
      EmployeeFilterReq filter, Pageable pageable) {

    Page<EmployeeViewDTO> employeesPage = employeeService.getEmployeesByFilter(filter, pageable);
    List<EmployeeViewDTO> content = employeesPage.getContent();

    List<Long> partyIds = content.stream().map(EmployeeViewDTO::partyId).distinct().toList();
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
                  PartyViewDTO party = partyMap.get(emp.partyId());

                  if (party == null) return null;

                  return new EmployeeFullViewDTO(emp, party);
                })
            .filter(Objects::nonNull)
            .toList();

    Page<EmployeeFullViewDTO> resultPage =
        new PageImpl<>(result, employeesPage.getPageable(), employeesPage.getTotalElements());
    log.info("employees returned {}", resultPage.getContent());
    return resultPage;
  }
}
