package com.attendancemanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.attendancemanagement.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> { }
