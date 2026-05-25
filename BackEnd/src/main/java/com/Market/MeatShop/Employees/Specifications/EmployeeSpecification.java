package com.Market.MeatShop.Employees.Specifications;

import com.Market.MeatShop.Employees.Entities.Employee;
import com.Market.MeatShop.Employees.Enums.EmployeeStatus;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EmployeeSpecification {

  public static Specification<Employee> asEmail(String email) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("email"), email));
  }
  public static Specification<Employee> betweenCreatingDates(LocalDateTime fromCreatedAt , LocalDateTime toDate ){
    if( fromCreatedAt!=null && toDate ==null){
      return ((root, query, criteriaBuilder) ->
              criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromCreatedAt));
    } else if (fromCreatedAt==null && toDate !=null) {
      return ((root, query, criteriaBuilder) ->
              criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate ));
    }

    return ((root, query, criteriaBuilder) ->
            criteriaBuilder.between(root.get("createdAt"), fromCreatedAt, toDate));
  }

  public static Specification<Employee> betweenUpdatingDates(LocalDateTime fromUpdatedAt , LocalDateTime toDate ){
    if( fromUpdatedAt!=null && toDate ==null){
      return ((root, query, criteriaBuilder) ->
              criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), fromUpdatedAt));
    } else if (fromUpdatedAt==null && toDate !=null) {
      return ((root, query, criteriaBuilder) ->
              criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), toDate ));
    }

    return ((root, query, criteriaBuilder) ->
            criteriaBuilder.between(root.get("updatedAt"), fromUpdatedAt, toDate));

  }

  public static Specification<Employee> betweenSalaries(
      BigDecimal minSalary, BigDecimal maxSalary) {
    if (minSalary != null && maxSalary == null) {
      return ((root, query, criteriaBuilder) ->
          criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), minSalary));
    } else if (minSalary == null && maxSalary != null) {
      return ((root, query, criteriaBuilder) ->
          criteriaBuilder.lessThanOrEqualTo(root.get("salary"), maxSalary));
    }
    if (minSalary.compareTo(maxSalary) > 0) {
      throw new IllegalArgumentException("minSalary must be <= maxSalary");
    }
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.between(root.get("salary"), minSalary, maxSalary));
  }

  public static Specification<Employee> asRole(Long roleId){
    return ((root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("roleId"), roleId));

  }

  public static Specification<Employee> asStatus(EmployeeStatus status){
    return ((root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), status));

  }

}
