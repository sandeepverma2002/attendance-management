package com.attendancemanagement.controller;

import org.springframework.web.bind.annotation.*;
import com.attendancemanagement.entity.Employee;
import com.attendancemanagement.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // Create Employee using JSON body
    @PostMapping("/create")
    public Employee create(@RequestBody Employee employee) {
        return service.create(employee);
    }

    // Get all Employees
    @GetMapping("/getall")
    public List<Employee> all() {
        return service.all();
    }
}
