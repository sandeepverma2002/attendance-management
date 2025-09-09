package com.attendancemanagement.dtos;




public record MarkAttendanceResponse(
     Long employeeId,
     String action,
     String eventTimeUtc,
     String eventLocalDate,
     String message
) {}

