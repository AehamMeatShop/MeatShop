package com.Market.MeatShop.Orchestas;

import com.Market.MeatShop.Employees.DTOs.EmployeeFullViewDTO;
import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;
import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmpContactReq;
import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmployeeReq;
import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Enums.EmployeeStatus;
import com.Market.MeatShop.Employees.Mappers.EmployeeMapper;
import com.Market.MeatShop.Employees.Services.EmployeeService;
import com.Market.MeatShop.Orchestas.requests.OrchCreateEmpReq;
import com.Market.MeatShop.Parties.DTOs.PartyContactViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.Enums.PartyType;
import com.Market.MeatShop.Parties.Services.PartyContactService;
import com.Market.MeatShop.Parties.Services.PartyService;
import com.Market.MeatShop.Security.Enums.SecuritySubjectType;
import com.Market.MeatShop.Security.Services.LoginIndexService;
import com.Market.MeatShop.Shared.Exceptions.PasswordCompromisedException;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EmploymentOrchestra {
  private final EmployeeService employeeService;
  private final PartyService partyService;
  private final PartyContactService parContService;
  private final LoginIndexService loginIndexService;

  public EmploymentOrchestra(
      EmployeeService employeeService,
      PartyService partyService,
      PartyContactService parContService,
      LoginIndexService loginIndexService) {
    this.employeeService = employeeService;
    this.partyService = partyService;
    this.parContService = parContService;
    this.loginIndexService = loginIndexService;
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
                req.name(),
                req.address(),
                req.email(),
                req.password(),
                req.salary(),
                partyId,
                req.status()));

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
    EmployeeFullViewDTO emp = employeeService.getEmployeeById(req.employeeId());

    CreatePartyContactReq partyContactReq =
        new CreatePartyContactReq(emp.partyInfo().id(), req.method(), req.identifier());
    PartyContactViewDTO resp = parContService.createPartyContact(partyContactReq);
    log.info("employee contact created {}", resp);
    return resp;
  }
}
