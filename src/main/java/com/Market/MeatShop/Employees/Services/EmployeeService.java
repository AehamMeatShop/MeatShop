package com.Market.MeatShop.Employees.Services;

import com.Market.MeatShop.Employees.DTOs.EmployeeFullViewDTO;
import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;
import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmpContactReq;
import com.Market.MeatShop.Employees.DTOs.Requests.CreateEmployeeReq;
import com.Market.MeatShop.Employees.DTOs.Requests.UpdateEmployeeProfileReq;
import com.Market.MeatShop.Employees.DTOs.Requests.UpdateEmployeeReq;
import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Mappers.EmployeeMapper;
import com.Market.MeatShop.Employees.Repositories.EmployeeRepo;
import com.Market.MeatShop.Parties.DTOs.PartyContactViewDTO;
import com.Market.MeatShop.Parties.DTOs.PartyViewDTO;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyContactReq;
import com.Market.MeatShop.Parties.DTOs.Requests.CreatePartyRequest;
import com.Market.MeatShop.Parties.DTOs.Requests.UpdatePartyReq;
import com.Market.MeatShop.Parties.DTOs.Responses.UpdatePartyResp;
import com.Market.MeatShop.Parties.Enums.PartyType;
import com.Market.MeatShop.Parties.Services.PartyContactService;
import com.Market.MeatShop.Parties.Services.PartyService;
import com.Market.MeatShop.Shared.Exceptions.TargetNotFound;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;


@Service
public class EmployeeService {
    private final EmployeeMapper employeeMapper;
    private final EmployeeRepo employeeRepo;
    private final PartyService partyService;
    private final PartyContactService partyContactService;
    public EmployeeService(EmployeeRepo employeeRepo, EmployeeMapper employeeMapper , PartyService partyService
    , PartyContactService partyContactService) {
        this.employeeRepo = employeeRepo;
        this.employeeMapper = employeeMapper;
        this.partyService = partyService;
        this.partyContactService = partyContactService;
    }

    public EmployeeViewDTO createEmployee(CreateEmployeeReq req){
        CreatePartyRequest partyReq= new CreatePartyRequest(
                req.name(), req.address(), PartyType.EMPLOYEE
        );
        Long partyId=partyService.createKnownParty(partyReq);

        Employee emp=new  Employee();
        emp.setRole(req.role());
        emp.setEmail(req.email());
        emp.setPassword(req.password());
        emp.setSalary(req.salary());
        emp.setStatus(req.status());
        emp.setPartyId(partyId);
        employeeRepo.save(emp);

        return employeeMapper.toEmployeeViewDTO(emp);

    }

    public PartyContactViewDTO createEmployeeContact(CreateEmpContactReq req){
        Employee emp=employeeRepo.findById(req.employeeId()).orElseThrow(()-> new TargetNotFound("employee not found"));
        CreatePartyContactReq partyContactReq= new CreatePartyContactReq(emp.getPartyId(), req.method() , req.identifier());
        return  partyContactService.createPartyContact(partyContactReq);
    }

    @Transactional
    public EmployeeFullViewDTO updateEmployee(UpdateEmployeeProfileReq req, Long id){
        Employee emp=employeeRepo.findById(id).orElseThrow(()-> new TargetNotFound("employee not found"));

       UpdatePartyResp partyResp = partyService.updateParty(new UpdatePartyReq(
                req.name(),
                req.address() ,
                null
        ) , emp.getPartyId());

        UpdateEmployeeReq empUpdReq=new UpdateEmployeeReq(
                req.email(),
                req.password(),
                req.salary(),
                req.role() ,
                req.status()
        );

        if(partyResp.updated()){
            emp = employeeMapper.updateFromReq(empUpdReq , emp);
            employeeRepo.save(emp);
        }
        else{
            Employee originalCopy = employeeMapper.clone(emp);
            emp = employeeMapper.updateFromReq(empUpdReq , emp);
            if(originalCopy.equals(emp)){
                throw new IllegalArgumentException("no changes");
            }
            employeeRepo.save(emp);
        }


        return new EmployeeFullViewDTO(employeeMapper.toEmployeeViewDTO(emp) , partyResp.partyInfo());





    }



}
