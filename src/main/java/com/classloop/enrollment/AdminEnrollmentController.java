package com.classloop.enrollment;

import com.classloop.auth.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/enrollments")
public class AdminEnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final JwtService jwtService;

    public AdminEnrollmentController(EnrollmentRepository enrollmentRepository, JwtService jwtService) {
        this.enrollmentRepository = enrollmentRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getAllEnrollments(Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return ResponseEntity.ok(enrollments);
    }

    @PostMapping
    public ResponseEntity<?> createEnrollment(@RequestBody CreateEnrollmentRequest request, Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        Enrollment enrollment = new Enrollment(request.userId, request.classId);
        enrollment = enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(enrollment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnrollment(@PathVariable UUID id, Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        if (!enrollmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        enrollmentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEnrollmentsByUser(@PathVariable UUID userId, Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getEnrollmentsByClass(@PathVariable UUID classId, Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByClassId(classId);
        return ResponseEntity.ok(enrollments);
    }

    private String extractToken(Authentication authentication) {
        return authentication.getCredentials() != null ? 
               authentication.getCredentials().toString() : "";
    }

    static class CreateEnrollmentRequest {
        public UUID userId;
        public UUID classId;
    }
}
