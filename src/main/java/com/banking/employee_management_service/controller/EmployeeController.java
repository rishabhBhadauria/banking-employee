package com.banking.employee_management_service.controller;

import com.banking.employee_management_service.dto.request.EmployeeRequest;
import com.banking.employee_management_service.dto.response.EmployeeResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    List<EmployeeResponse> employeeResponses = new ArrayList<EmployeeResponse>();
    @PostMapping("/create-employee")
    public void addEmployee(@RequestBody @Valid EmployeeRequest employeeRequest){

       EmployeeResponse emp = new EmployeeResponse(employeeRequest.getId(),
               employeeRequest.getName(),
               employeeRequest.getDepartment(),
               employeeRequest.getSalary());
       employeeResponses.add(emp);
    }

    @GetMapping("/fetch-all")
    public List<EmployeeResponse> fetchAllEmployees(){
        return employeeResponses;
    }

    @GetMapping("{id}")
    public EmployeeResponse getEmployee(@PathVariable int id){
        for (EmployeeResponse emp : employeeResponses){
            if (emp.getId() == id){
                return emp;
            }
        }
        return null;
    }

    @PutMapping("{id}")
    public void updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest employeeRequest){
        for (EmployeeResponse emp : employeeResponses){
            if (emp.getId() == id){
                emp.setSalary(employeeRequest.getSalary());
                emp.setName(employeeRequest.getName());
                emp.setId(id);
                emp.setDepartment(employeeRequest.getDepartment());
            }
        }
    }
}
