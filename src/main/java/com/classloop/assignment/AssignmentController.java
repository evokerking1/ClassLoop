package com.classloop.assignment;

import com.classloop.auth.JwtService;
import com.classloop.classroom.Classroom;
import com.classloop.classroom.ClassroomRepository;
import com.classloop.enrollment.Enrollment;
import com.classloop.enrollment.EnrollmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final JwtService jwtService;

    public AssignmentController(AssignmentRepository assignmentRepository,
                               ClassroomRepository classroomRepository,
                               EnrollmentRepository enrollmentRepository,
                               JwtService jwtService) {
        this.assignmentRepository = assignmentRepository;
        this.classroomRepository = classroomRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAssignments(Authentication authentication) {
        String token = extractToken(authentication);
        UUID userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        List<Assignment> assignments;
        if ("TEACHER".equals(role)) {
            List<UUID> classIds = classroomRepository.findByTeacherId(userId)
                    .stream()
                    .map(Classroom::getId)
                    .collect(Collectors.toList());
            assignments = assignmentRepository.findByClassIdIn(classIds);
        } else {
            List<UUID> classIds = enrollmentRepository.findByUserId(userId)
                    .stream()
                    .map(Enrollment::getClassId)
                    .collect(Collectors.toList());
            assignments = assignmentRepository.findByClassIdIn(classIds);
        }

        return ResponseEntity.ok(assignments);
    }

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody CreateAssignmentRequest request, Authentication authentication) {
        String token = extractToken(authentication);
        UUID userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        if (!"TEACHER".equals(role)) {
            return ResponseEntity.status(403).body("Only teachers can create assignments");
        }

        // Verify teacher owns the class
        Classroom classroom = classroomRepository.findById(request.classId).orElse(null);
        if (classroom == null || !classroom.getTeacherId().equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        Assignment assignment = new Assignment(
                request.classId,
                request.title,
                request.description,
                request.dueDate,
                request.type,
                request.estimatedWorkloadMinutes
        );
        assignment = assignmentRepository.save(assignment);

        return ResponseEntity.ok(assignment);
    }

    private String extractToken(Authentication authentication) {
        return authentication.getCredentials() != null ? 
               authentication.getCredentials().toString() : "";
    }

    static class CreateAssignmentRequest {
        public UUID classId;
        public String title;
        public String description;
        public ZonedDateTime dueDate;
        public Assignment.AssignmentType type;
        public Integer estimatedWorkloadMinutes;
    }
}
