package com.attendancemanagement.service;


import org.springframework.stereotype.Service;

import com.attendancemanagement.entity.Employee;
import com.attendancemanagement.repository.EmployeeRepository;

import java.util.List;

@Service
public class EmployeeService {
 private final EmployeeRepository repo;

 public EmployeeService(EmployeeRepository repo) {
     this.repo = repo;
 }

 public Employee create(Employee e) {
     return repo.save(e);
 }

 public List<Employee> all() {
     return repo.findAll();
 }

 public Employee getOrThrow(Long id) {
     return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
 }
}
