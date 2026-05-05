package com.Market.MeatShop.Employees.Services;

import com.Market.MeatShop.Employees.DTOs.EmployeeFullViewDTO;
import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;
import com.Market.MeatShop.Employees.DTOs.Requests.*;
import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Enums.EmployeeRole;
import com.Market.MeatShop.Employees.Mappers.EmployeeMapper;
import com.Market.MeatShop.Employees.QueryRoles.EmployeeQueryRoles;
import com.Market.MeatShop.Employees.Repositories.EmployeeRepo;
import com.Market.MeatShop.Employees.Specifications.EmployeeSpecification;
import com.Market.MeatShop.Parties.DTOs.PartyContactViewDTO;
import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.PartyFilterReq;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.DTOs.Responses.UpdatePartyResp;
import com.Market.MeatShop.Parties.Entities.Party;
import com.Market.MeatShop.Parties.Enums.PartyType;
import com.Market.MeatShop.Parties.QueryRoles.PartyQueryRoles;
import com.Market.MeatShop.Parties.Services.PartyContactService;
import com.Market.MeatShop.Parties.Services.PartyService;
import com.Market.MeatShop.Parties.Spesifications.PartySpecifications;
import com.Market.MeatShop.Products.QueryRoles.ProductQueryRoles;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tools.jackson.databind.util.FullyNamed;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
  private final EmployeeMapper employeeMapper;
  private final EmployeeRepo employeeRepo;
  private final PartyService partyService;
  private final PartyContactService partyContactService;

  public EmployeeService(
      EmployeeRepo employeeRepo,
      EmployeeMapper employeeMapper,
      PartyService partyService,
      PartyContactService partyContactService) {
    this.employeeRepo = employeeRepo;
    this.employeeMapper = employeeMapper;
    this.partyService = partyService;
    this.partyContactService = partyContactService;
  }

  public EmployeeViewDTO createEmployee(CreateEmployeeReq req) {
    CreatePartyRequest partyReq =
        new CreatePartyRequest(req.name(), req.address(), PartyType.EMPLOYEE);
    Long partyId = partyService.createParty(partyReq).id();

    Employee emp = new Employee();
    emp.setRole(req.role());
    emp.setEmail(req.email());
    emp.setPassword(req.password());
    emp.setSalary(req.salary());
    emp.setStatus(req.status());
    emp.setPartyId(partyId);
    employeeRepo.save(emp);

    return employeeMapper.toEmployeeViewDTO(emp);
  }

  public PartyContactViewDTO createEmployeeContact(CreateEmpContactReq req) {
    Employee emp =
        employeeRepo
            .findById(req.employeeId())
            .orElseThrow(() -> new TargetNotFound("employee not found"));
    CreatePartyContactReq partyContactReq =
        new CreatePartyContactReq(emp.getPartyId(), req.method(), req.identifier());
    return partyContactService.createPartyContact(partyContactReq);
  }

  @Transactional
  public EmployeeFullViewDTO updateEmployee(UpdateEmployeeProfileReq req, Long id) {
    Employee emp =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));

    UpdatePartyResp partyResp =
        partyService.updateParty(
            new UpdatePartyReq(req.name(), req.address(), null), emp.getPartyId());

    UpdateEmployeeReq empUpdReq =
        new UpdateEmployeeReq(req.email(), req.password(), req.salary(), req.role(), req.status());

    if (partyResp.updated()) {
      emp = employeeMapper.updateFromReq(empUpdReq, emp);
      employeeRepo.save(emp);
    } else {
      Employee originalCopy = employeeMapper.clone(emp);
      emp = employeeMapper.updateFromReq(empUpdReq, emp);
      if (originalCopy.equals(emp)) {
        throw new IllegalArgumentException("no changes");
      }
      employeeRepo.save(emp);
    }

    return new EmployeeFullViewDTO(employeeMapper.toEmployeeViewDTO(emp), partyResp.partyInfo());
  }

  @Transactional
  public void deleteEmployee(Long id) {
    Employee employee =
        employeeRepo.findById(id).orElseThrow(() -> new TargetNotFound("employee not found"));
    if (employee.getRole().equals(EmployeeRole.SUPER_ADMIN)) {
      throw new IllegalArgumentException("cannot delete super admin");
    }
    partyService.deleteParty(employee.getPartyId());
    employeeRepo.delete(employee);
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
          "Page size is greater than " + ProductQueryRoles.maxPageSize);
    }
    Specification<Employee> spec = Specification.allOf();
    if (filter.email() != null) {
      spec = spec.and(EmployeeSpecification.asEmail(filter.email()));
    }
    if (filter.role() != null) {
      spec = spec.and(EmployeeSpecification.asRole(filter.role()));
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

    List<Long> partyIds=content.stream().map(Employee::getPartyId).distinct().toList();
    PartyFilterReq partyFilterReq=new PartyFilterReq(
            null,
            filter.name(),
            filter.address(),
            PartyType.EMPLOYEE,
            null,
            null,null,
            null
    );
    List<PartyViewDTO> parties=partyService.findByFilterServ(partyFilterReq , partyIds);
    Map<Long, PartyViewDTO> partyMap = parties.stream()
            .collect(Collectors.toMap(
                    PartyViewDTO::id,
                    p -> p
            ));

    List<EmployeeFullViewDTO> result = content.stream()
            .map(emp -> {
              PartyViewDTO party = partyMap.get(emp.getPartyId());

              if (party == null) return null;

              return new EmployeeFullViewDTO(
                      employeeMapper.toEmployeeViewDTO(emp),
                      party
              );
            })
            .filter(Objects::nonNull)
            .toList();

    Page<EmployeeFullViewDTO> resultPage =
            new PageImpl<>(
                    result,
                    employeesPage.getPageable(),
                    employeesPage.getTotalElements()
            );
    return resultPage;

  }
}
