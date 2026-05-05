package com.Market.MeatShop.Employees.Mappers;

import com.Market.MeatShop.Employees.DTOs.EmployeeViewDTO;
import com.Market.MeatShop.Employees.DTOs.Requests.UpdateEmployeeReq;
import com.Market.MeatShop.Employees.Entities.Employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeViewDTO toEmployeeViewDTO(Employee employee);
    Employee toEmployee(EmployeeViewDTO employeeViewDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Employee updateFromReq(UpdateEmployeeReq req, @MappingTarget Employee emp);
    Employee clone(Employee emp);
}
