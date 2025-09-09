package com.attendancemanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.attendancemanagement.entity.AttendanceEvent;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AttendanceEventRepository extends JpaRepository<AttendanceEvent, Long> {

 Optional<AttendanceEvent> findTopByEmployee_IdOrderByEventTimeDesc(Long employeeId);

 Optional<AttendanceEvent> findTopByEmployee_IdAndEventTimeLessThanOrderByEventTimeDesc(Long employeeId, Instant before);

 List<AttendanceEvent> findByEmployee_IdAndEventTimeBetweenOrderByEventTimeAsc(Long employeeId, Instant from, Instant to);
}
