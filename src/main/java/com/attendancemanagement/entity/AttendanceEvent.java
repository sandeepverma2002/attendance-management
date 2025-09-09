package com.attendancemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

import com.attendancemanagement.enums.ActionType;

@Entity
@Table(name = "attendance_events", indexes = {
     @Index(name = "idx_attendance_emp_time", columnList = "employee_id,event_time"),
     @Index(name = "idx_attendance_emp_date", columnList = "employee_id,event_date")
})
@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class AttendanceEvent {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(optional = false, fetch = FetchType.LAZY)
 @JoinColumn(name = "employee_id")
 private Employee employee;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false, length = 20)
 private ActionType action;

 /** UTC timestamp of the event */
 @Column(name = "event_time", nullable = false)
 private Instant eventTime;

 /** Event local calendar date for indexing/reporting (e.g., Asia/Kolkata) */
 @Column(name = "event_date", nullable = false)
 private LocalDate eventDate;

 @Column(nullable = false, updatable = false)
 private Instant createdAt;

 @PrePersist
 public void prePersist() {
     if (createdAt == null) createdAt = Instant.now();
 }
}
