package com.classloop.timeline;

import com.classloop.assignment.Assignment;
import com.classloop.assignment.AssignmentRepository;
import com.classloop.auth.JwtService;
import com.classloop.classroom.Classroom;
import com.classloop.classroom.ClassroomRepository;
import com.classloop.enrollment.Enrollment;
import com.classloop.enrollment.EnrollmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    private final AssignmentRepository assignmentRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final JwtService jwtService;

    public TimelineController(AssignmentRepository assignmentRepository,
                             ClassroomRepository classroomRepository,
                             EnrollmentRepository enrollmentRepository,
                             JwtService jwtService) {
        this.assignmentRepository = assignmentRepository;
        this.classroomRepository = classroomRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<TimelineItem>> getTimeline(Authentication authentication) {
        String token = extractToken(authentication);
        UUID userId = jwtService.extractUserId(token);

        // Get all enrolled classes
        List<UUID> classIds = enrollmentRepository.findByUserId(userId)
                .stream()
                .map(Enrollment::getClassId)
                .collect(Collectors.toList());

        if (classIds.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Get all assignments
        List<Assignment> assignments = assignmentRepository.findByClassIdIn(classIds);

        // Get all classes for names
        Map<UUID, String> classNames = classroomRepository.findAllById(classIds)
                .stream()
                .collect(Collectors.toMap(Classroom::getId, Classroom::getName));

        // Calculate conflict levels
        Map<UUID, String> conflictLevels = calculateConflictLevels(assignments);

        // Build timeline items
        List<TimelineItem> timeline = assignments.stream()
                .map(assignment -> new TimelineItem(
                        assignment.getId(),
                        classNames.getOrDefault(assignment.getClassId(), "Unknown"),
                        assignment.getTitle(),
                        assignment.getDueDate(),
                        assignment.getEstimatedWorkloadMinutes() != null ? assignment.getEstimatedWorkloadMinutes() : 0,
                        conflictLevels.getOrDefault(assignment.getId(), "LOW")
                ))
                .sorted(Comparator.comparing(TimelineItem::dueDate))
                .collect(Collectors.toList());

        return ResponseEntity.ok(timeline);
    }

    private Map<UUID, String> calculateConflictLevels(List<Assignment> assignments) {
        Map<UUID, String> conflictLevels = new HashMap<>();

        for (Assignment assignment : assignments) {
            ZonedDateTime dueDate = assignment.getDueDate();
            
            // Find all assignments within 24 hours
            int totalWorkload = assignments.stream()
                    .filter(a -> {
                        long hoursDiff = Math.abs(
                            java.time.Duration.between(a.getDueDate(), dueDate).toHours()
                        );
                        return hoursDiff <= 24;
                    })
                    .mapToInt(a -> a.getEstimatedWorkloadMinutes() != null ? a.getEstimatedWorkloadMinutes() : 0)
                    .sum();

            String level;
            if (totalWorkload < 60) {
                level = "LOW";
            } else if (totalWorkload <= 120) {
                level = "MEDIUM";
            } else {
                level = "HIGH";
            }

            conflictLevels.put(assignment.getId(), level);
        }

        return conflictLevels;
    }

    private String extractToken(Authentication authentication) {
        return authentication.getCredentials() != null ? 
               authentication.getCredentials().toString() : "";
    }

    record TimelineItem(
        UUID assignmentId,
        String className,
        String title,
        ZonedDateTime dueDate,
        int workload,
        String conflictLevel
    ) {}
}
