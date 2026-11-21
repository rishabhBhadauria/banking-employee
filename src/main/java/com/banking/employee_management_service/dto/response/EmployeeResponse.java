package com.banking.employee_management_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String name;
    private String department;
    private BigDecimal salary;
}
