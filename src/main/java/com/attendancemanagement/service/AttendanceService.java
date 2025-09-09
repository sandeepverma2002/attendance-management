package com.attendancemanagement.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.attendancemanagement.dtos.AttendanceReportResponse;
import com.attendancemanagement.dtos.DaySummaryDto;
import com.attendancemanagement.dtos.DurationSummaryDto;
import com.attendancemanagement.dtos.MarkAttendanceResponse;
import com.attendancemanagement.dtos.SessionDto;
import com.attendancemanagement.entity.AttendanceEvent;
import com.attendancemanagement.entity.Employee;
import com.attendancemanagement.enums.ActionType;
import com.attendancemanagement.repository.AttendanceEventRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AttendanceService {

 private final AttendanceEventRepository repo;
 private final EmployeeService employeeService;
 private final ZoneId appZone;

 private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
 private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

 public AttendanceService(AttendanceEventRepository repo, EmployeeService employeeService, ZoneId appZoneId) {
     this.repo = repo;
     this.employeeService = employeeService;
     this.appZone = appZoneId;
 }

 @Transactional
 public MarkAttendanceResponse mark(Long employeeId) {
     Employee emp = employeeService.getOrThrow(employeeId);

     Optional<AttendanceEvent> lastOpt = repo.findTopByEmployee_IdOrderByEventTimeDesc(employeeId);
     ActionType next;
     if (lastOpt.isEmpty() || lastOpt.get().getAction() == ActionType.PUNCH_OUT) {
         next = ActionType.PUNCH_IN;
     } else {
         next = ActionType.PUNCH_OUT;
     }

     Instant now = Instant.now();
     LocalDate localDate = LocalDateTime.ofInstant(now, appZone).toLocalDate();

     AttendanceEvent ev = AttendanceEvent.builder()
             .employee(emp)
             .action(next)
             .eventTime(now)
             .eventDate(localDate)
             .build();
     repo.save(ev);

     return new MarkAttendanceResponse(
             employeeId,
             next.name(),
             now.toString(),
             localDate.format(DATE_FMT),
             "Attendance " + next.name().replace('_',' ').toLowerCase() + " recorded successfully."
     );
 }

 public AttendanceReportResponse report(Long employeeId, Instant from, Instant to, ZoneId tz) {
     // Include event just before 'from' to pair sessions correctly
     Optional<AttendanceEvent> pre = repo.findTopByEmployee_IdAndEventTimeLessThanOrderByEventTimeDesc(employeeId, from);
     List<AttendanceEvent> inRange = repo.findByEmployee_IdAndEventTimeBetweenOrderByEventTimeAsc(employeeId, from, to);

     List<AttendanceEvent> events = new ArrayList<>();
     pre.ifPresent(events::add);
     events.addAll(inRange);

     // Pair up IN/OUT
     List<Session> sessions = toSessions(events, from, to);

     // Split sessions by day in requested timezone
     Map<LocalDate, List<SessionDto>> dayMap = new LinkedHashMap<>();
     long grandTotal = 0;
     int totalSessions = 0;

     for (Session s : sessions) {
         List<Split> pieces = splitByDay(s.start, s.end, tz);
         boolean open = s.open;
         for (int i = 0; i < pieces.size(); i++) {
             Split p = pieces.get(i);
             LocalDate d = p.date;
             long mins = Duration.between(p.start, p.end).toMinutes();
             grandTotal += mins;
             // Only the last piece of an open session is considered "open"
             boolean pieceOpen = open && (i == pieces.size() - 1);

             dayMap.computeIfAbsent(d, k -> new ArrayList<>())
                   .add(new SessionDto(
                           p.start.format(DT_FMT),
                           pieceOpen ? null : p.end.format(DT_FMT),
                           mins,
                           pieceOpen
                   ));
         }
         totalSessions++;
     }

     // Build day summaries
     List<DaySummaryDto> days = new ArrayList<>();
     for (Map.Entry<LocalDate, List<SessionDto>> e : dayMap.entrySet()) {
         long sum = e.getValue().stream().mapToLong(SessionDto::durationMinutes).sum();
         days.add(new DaySummaryDto(e.getKey().format(DATE_FMT), e.getValue(), sum));
     }

     int workingDays = (int) dayMap.keySet().size();
     long avg = workingDays == 0 ? 0 : Math.round((double) grandTotal / workingDays);

     DurationSummaryDto summary = new DurationSummaryDto(
             workingDays,
             totalSessions,
             grandTotal,
             avg
     );

     return new AttendanceReportResponse(
             employeeId,
             from.toString(),
             to.toString(),
             tz.getId(),
             days,
             summary
     );
 }

 // ----- Helpers -----

 private record Session(Instant start, Instant end, boolean open) {}
 private record Split(LocalDate date, LocalDateTime start, LocalDateTime end) {}

 /** Build paired sessions, handling leading OUT or trailing IN gracefully. */
 private List<Session> toSessions(List<AttendanceEvent> events, Instant from, Instant to) {
     List<Session> result = new ArrayList<>();
     AttendanceEvent prev = null;
     for (AttendanceEvent e : events) {
         if (prev == null) {
             prev = e;
             continue;
         }
         if (prev.getAction() == ActionType.PUNCH_IN && e.getAction() == ActionType.PUNCH_OUT) {
             // Proper pair
             Instant s = prev.getEventTime().isBefore(from) ? from : prev.getEventTime();
             Instant end = e.getEventTime().isAfter(to) ? to : e.getEventTime();
             if (s.isBefore(end)) {
                 result.add(new Session(s, end, false));
             }
             prev = null;
         } else {
             // If we see two INs or two OUTs in a row, reset to the latest
             prev = e;
         }
     }
     // Trailing open session (IN without OUT)
     if (prev != null && prev.getAction() == ActionType.PUNCH_IN) {
         Instant s = prev.getEventTime().isBefore(from) ? from : prev.getEventTime();
         Instant end = Instant.now().isBefore(to) ? Instant.now() : to;
         if (s.isBefore(end)) {
             result.add(new Session(s, end, true));
         }
     }
     return result;
 }

 /** Split a UTC interval into per-day chunks in a target timezone. */
 private List<Split> splitByDay(Instant startUtc, Instant endUtc, ZoneId tz) {
     List<Split> out = new ArrayList<>();
     LocalDateTime start = LocalDateTime.ofInstant(startUtc, tz);
     LocalDateTime end = LocalDateTime.ofInstant(endUtc, tz);

     LocalDateTime curStart = start;
     while (true) {
         LocalDate curDate = curStart.toLocalDate();
         LocalDateTime dayEnd = curDate.plusDays(1).atStartOfDay();
         LocalDateTime curEnd = end.isBefore(dayEnd) ? end : dayEnd;
         out.add(new Split(curDate, curStart, curEnd));
         if (!curEnd.isBefore(end)) break;
         curStart = curEnd;
     }
     return out;
 }
}
