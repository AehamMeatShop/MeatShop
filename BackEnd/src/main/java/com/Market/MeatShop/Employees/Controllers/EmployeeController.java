package com.Market.MeatShop.Employees.Controllers;

import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmpContactReq;
import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmployeeReq;
import com.Market.MeatShop.Employees.DTOs.Requests.EmployeeFilterReq;
import com.Market.MeatShop.Employees.DTOs.Requests.UpdateEmployeeProfileReq;
import com.Market.MeatShop.Employees.Services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
  private final EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }
  @GetMapping("/health-check")
  public ResponseEntity<?> healthCheck()
  {
    return ResponseEntity.ok().build();
  }
  @PostMapping
  public ResponseEntity<?> createEmployee(@Valid @RequestBody CreateEmployeeReq req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(req));
  }

  @PostMapping("/contact")
  public ResponseEntity<?> createEmployeContact(@Valid @RequestBody CreateEmpContactReq req) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(employeeService.createEmployeeContact(req));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateEmployeeProfile(
      @Valid @RequestBody UpdateEmployeeProfileReq req, @PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(employeeService.updateEmployee(req, id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeById(id));
  }
  @GetMapping
  public ResponseEntity<?> getEmployeesByFilter(EmployeeFilterReq filter, Pageable pageable){
    return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeesByFilter(filter , pageable));
  }

}
