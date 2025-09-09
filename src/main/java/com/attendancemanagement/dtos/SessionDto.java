package com.attendancemanagement.dtos;



public record SessionDto(
     String inTime,       // ISO date-time in requested timezone
     String outTime,      // may be null if open
     long durationMinutes,
     boolean open
) {}
