package com.attendancemanagement.dtos;


import java.util.List;

public record AttendanceReportResponse(
     Long employeeId,
     String from,
     String to,
     String timezone,
     List<DaySummaryDto> days,
     DurationSummaryDto summary
) {}
