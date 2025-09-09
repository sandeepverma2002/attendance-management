package com.attendancemanagement.controller;


import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.attendancemanagement.dtos.AttendanceReportResponse;
import com.attendancemanagement.dtos.MarkAttendanceRequest;
import com.attendancemanagement.dtos.MarkAttendanceResponse;
import com.attendancemanagement.service.AttendanceService;

import java.time.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

 private final AttendanceService service;

 public AttendanceController(AttendanceService service) {
     this.service = service;
 }

 /** POST /api/attendance/mark  body: { "employeeId": 1 } */
 @PostMapping("/mark")
 public MarkAttendanceResponse mark(@Valid @RequestBody MarkAttendanceRequest req) {
     return service.mark(req.employeeId());
 }

 /** GET /api/attendance/report?employeeId=1&from=2025-09-01T00:00:00Z&to=2025-09-09T23:59:59Z&tz=Asia/Kolkata */
 @GetMapping("/report")
 public AttendanceReportResponse report(
         @RequestParam Long employeeId,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
         @RequestParam(defaultValue = "Asia/Kolkata") String tz
 ) {
     ZoneId zone = ZoneId.of(tz);
     return service.report(employeeId, from, to, zone);
 }
}
