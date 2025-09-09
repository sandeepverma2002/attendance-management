package com.attendancemanagement.dtos;



import java.util.List;

public record DaySummaryDto(
     String date,                 // yyyy-MM-dd (in requested timezone)
     List<SessionDto> sessions,
     long totalWorkMinutes
) {}

