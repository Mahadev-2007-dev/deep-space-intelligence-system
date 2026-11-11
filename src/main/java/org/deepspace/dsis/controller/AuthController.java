package org.deepspace.dsis.controller;

import org.deepspace.dsis.model.User;
import org.deepspace.dsis.repository.UserRepository;
import org.deepspace.dsis.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
        public String fullName;
        public String role;
        public String callsign;
        public String department;
    }

    public static class UpdateProfileRequest {
        public String fullName;
        public String callsign;
        public String department;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.username == null || req.password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password required."));
        }

        User user = userRepository.findByUsername(req.username.trim()).orElse(null);
        if (user == null || !passwordEncoder.matches(req.password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid flight credentials."));
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole(), user.getCallsign());

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("id", user.getId());
        resp.put("username", user.getUsername());
        resp.put("email", user.getEmail());
        resp.put("fullName", user.getFullName());
        resp.put("role", user.getRole());
        resp.put("callsign", user.getCallsign());
        resp.put("department", user.getDepartment());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.username == null || req.username.isBlank() || req.password == null || req.password.isBlank() || req.email == null || req.email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username, email, and password are required."));
        }

        if (userRepository.existsByUsername(req.username.trim())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username is already registered in flight operations registry."));
        }

        if (userRepository.existsByEmail(req.email.trim())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is already registered."));
        }

        String role = (req.role != null && req.role.toUpperCase().contains("ADMIN")) ? "ROLE_ADMIN" : "ROLE_OPERATOR";
        String callsign = (req.callsign != null && !req.callsign.isBlank()) ? req.callsign.toUpperCase() : "CADET_" + System.currentTimeMillis() % 1000;
        String dept = (req.department != null && !req.department.isBlank()) ? req.department : "Mission Operations Division";

        User user = new User(
                req.username.trim(),
                passwordEncoder.encode(req.password),
                req.email.trim(),
                req.fullName != null ? req.fullName.trim() : req.username,
                role,
                callsign,
                dept
        );

        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername(), user.getRole(), user.getCallsign());

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("id", user.getId());
        resp.put("username", user.getUsername());
        resp.put("email", user.getEmail());
        resp.put("fullName", user.getFullName());
        resp.put("role", user.getRole());
        resp.put("callsign", user.getCallsign());
        resp.put("department", user.getDepartment());

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            // Return default flight director for demo if not logged in
            User fallback = userRepository.findByUsername("admin").orElse(null);
            if (fallback != null) {
                return ResponseEntity.ok(Map.of(
                        "username", fallback.getUsername(),
                        "role", fallback.getRole(),
                        "callsign", fallback.getCallsign(),
                        "fullName", fallback.getFullName(),
                        "department", fallback.getDepartment(),
                        "authenticated", false
                ));
            }
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }

        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole(),
                "callsign", user.getCallsign(),
                "department", user.getDepartment(),
                "authenticated", true
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest req, Authentication auth) {
        String username = (auth != null && auth.getName() != null) ? auth.getName() : "admin";
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found."));
        }

        if (req.fullName != null && !req.fullName.isBlank()) user.setFullName(req.fullName);
        if (req.callsign != null && !req.callsign.isBlank()) user.setCallsign(req.callsign);
        if (req.department != null && !req.department.isBlank()) user.setDepartment(req.department);
        if (req.password != null && !req.password.isBlank()) user.setPassword(passwordEncoder.encode(req.password));

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully.", "user", user));
    }
}
