package com.classloop.classroom;

import com.classloop.auth.JwtService;
import com.classloop.util.ControllerUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/classes")
public class AdminClassroomController {

    private final ClassroomRepository classroomRepository;
    private final JwtService jwtService;

    public AdminClassroomController(ClassroomRepository classroomRepository, JwtService jwtService) {
        this.classroomRepository = classroomRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getAllClasses(HttpServletRequest request) {
        String token = ControllerUtils.extractToken(request);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        List<Classroom> classes = classroomRepository.findAll();
        return ResponseEntity.ok(classes);
    }
}
