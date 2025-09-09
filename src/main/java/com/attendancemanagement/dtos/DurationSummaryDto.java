package com.attendancemanagement.dtos;


public record DurationSummaryDto(
     int totalDaysWithWork,
     int totalSessions,
     long totalWorkMinutes,
     long averageMinutesPerWorkingDay
) {}
