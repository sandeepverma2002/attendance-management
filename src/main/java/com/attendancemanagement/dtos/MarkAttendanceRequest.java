package com.attendancemanagement.dtos;


import jakarta.validation.constraints.NotNull;

public record MarkAttendanceRequest(
     @NotNull Long employeeId
) {}

