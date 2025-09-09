package com.attendancemanagement.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "employees", indexes = {
     @Index(name = "idx_employee_name", columnList = "name")
})
@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class Employee {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String name;

 private String department;

 @Column(nullable = false, updatable = false)
 private Instant createdAt;

 @PrePersist
 public void prePersist() {
     if (createdAt == null) createdAt = Instant.now();
 }
}

