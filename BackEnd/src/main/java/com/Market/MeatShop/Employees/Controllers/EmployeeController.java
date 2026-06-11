package com.Market.MeatShop.Employees.Controllers;

import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmpContactReq;
import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmployeeReq;
import com.Market.MeatShop.Employees.DTOs.Requests.EmployeeFilterReq;
import com.Market.MeatShop.Employees.DTOs.Requests.UpdateEmployeeProfileReq;
import com.Market.MeatShop.Employees.Services.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {
  private final EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @GetMapping("/health-check")
  public ResponseEntity<?> healthCheck() {
    log.info("GET /employees/health-check requested");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasAuthority('EMPLOYEE_MANAGEMENT')")
  @PostMapping
  public ResponseEntity<?> createEmployee(@Valid @RequestBody CreateEmployeeReq req) {
    log.info("POST /employees {} requested", req);
    return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(req));
  }

  @PreAuthorize("hasAuthority('EMPLOYEE_MANAGEMENT')")
  @PostMapping("/contact")
  public ResponseEntity<?> createEmployeContact(@Valid @RequestBody CreateEmpContactReq req) {
    log.info("POST /employees/contact {} requested", req);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(employeeService.createEmployeeContact(req));
  }

  @PreAuthorize("hasAuthority('EMPLOYEE_MANAGEMENT')")
  @PutMapping("/{id}")
  public ResponseEntity<?> updateEmployeeProfile(
      @Valid @RequestBody UpdateEmployeeProfileReq req, @PathVariable Long id) {
    log.info("PUT /employees/{} requested", id);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(employeeService.updateEmployee(req, id));
  }

  @PreAuthorize("hasAuthority('EMPLOYEE_MANAGEMENT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
    log.info("DELETE /employees/{} requested", id);
    employeeService.deleteEmployee(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PreAuthorize("hasAuthority('EMPLOYEE_MANAGEMENT')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
    log.info("GET /employees/{} requested", id);
    return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeById(id));
  }

  @PreAuthorize("hasAuthority('EMPLOYEE_MANAGEMENT')")
  @GetMapping
  public ResponseEntity<?> getEmployeesByFilter(EmployeeFilterReq filter, Pageable pageable) {
    log.info("GET /employees query{} requested", filter);
    return ResponseEntity.status(HttpStatus.OK)
        .body(employeeService.getEmployeesByFilter(filter, pageable));
  }

  @PostMapping("/start-application")
  public ResponseEntity<?> startApplication(@RequestBody @Valid CreateEmployeeReq request) {
    log.info("POST /employees/start-application requested");
    return ResponseEntity.status(HttpStatus.OK).body(employeeService.startApplication(request));
  }
}
