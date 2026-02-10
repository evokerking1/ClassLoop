package com.classloop.user;

import com.classloop.auth.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request, Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        if (userRepository.findByEmail(request.email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User(
                request.email,
                passwordEncoder.encode(request.password),
                request.role
        );
        user = userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request, Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.email != null) {
            user.setEmail(request.email);
        }
        if (request.role != null) {
            user.setRole(request.role);
        }
        if (request.password != null && !request.password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.password));
        }

        user = userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id, Authentication authentication) {
        String token = extractToken(authentication);
        String role = jwtService.extractRole(token);

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private String extractToken(Authentication authentication) {
        return authentication.getCredentials() != null ? 
               authentication.getCredentials().toString() : "";
    }

    static class CreateUserRequest {
        public String email;
        public String password;
        public User.Role role;
    }

    static class UpdateUserRequest {
        public String email;
        public String password;
        public User.Role role;
    }
}
