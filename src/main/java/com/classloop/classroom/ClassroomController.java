package com.classloop.classroom;

import com.classloop.auth.JwtService;
import com.classloop.enrollment.Enrollment;
import com.classloop.enrollment.EnrollmentRepository;
import com.classloop.util.ControllerUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
public class ClassroomController {

    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final JwtService jwtService;

    public ClassroomController(ClassroomRepository classroomRepository, 
                              EnrollmentRepository enrollmentRepository,
                              JwtService jwtService) {
        this.classroomRepository = classroomRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<Classroom>> getClasses(HttpServletRequest request) {
        String token = ControllerUtils.extractToken(request);
        UUID userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        List<Classroom> classes;
        if ("TEACHER".equals(role)) {
            classes = classroomRepository.findByTeacherId(userId);
        } else {
            List<UUID> classIds = enrollmentRepository.findByUserId(userId)
                    .stream()
                    .map(Enrollment::getClassId)
                    .collect(Collectors.toList());
            classes = classroomRepository.findAllById(classIds);
        }

        return ResponseEntity.ok(classes);
    }

    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody CreateClassRequest request, HttpServletRequest httpRequest) {
        String token = ControllerUtils.extractToken(httpRequest);
        UUID userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        if (!"TEACHER".equals(role)) {
            return ResponseEntity.status(403).body("Only teachers can create classes");
        }

        Classroom classroom = new Classroom(
                request.name,
                request.subject,
                userId,
                request.period
        );
        classroom = classroomRepository.save(classroom);

        return ResponseEntity.ok(classroom);
    }

    static class CreateClassRequest {
        public String name;
        public String subject;
        public Integer period;
    }
}
