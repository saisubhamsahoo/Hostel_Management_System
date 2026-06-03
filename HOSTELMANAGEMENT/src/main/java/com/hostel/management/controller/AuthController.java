package com.hostel.management.controller;

import com.hostel.management.model.User;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailUtil emailUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                response.put("success", false);
                response.put("message", "Email already registered");
                return ResponseEntity.badRequest().body(response);
            }

            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(24));
            user.setVerified(false);

            User savedUser = userRepository.save(user);

            emailUtil.sendVerificationEmail(user.getEmail(), token);

            response.put("success", true);
            response.put("message", "Registration successful! Please check your email to verify your account.");
            response.put("userId", savedUser.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOpt = userRepository.findByVerificationToken(token);

            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid verification token");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userOpt.get();

            if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
                response.put("success", false);
                response.put("message", "Verification token has expired");
                return ResponseEntity.badRequest().body(response);
            }

            user.setVerified(true);
            user.setVerificationToken(null);
            user.setTokenExpiry(null);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Email verified successfully! You can now login.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userOpt.get();

            if (!user.getVerified()) {
                response.put("success", false);
                response.put("message", "Please verify your email before logging in");
                return ResponseEntity.badRequest().body(response);
            }

            if (!user.getPassword().equals(password)) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", createUserResponse(user));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("phone", user.getPhone());
        userMap.put("role", user.getRole().toString());
        userMap.put("roomId", user.getRoomId());
        return userMap;
    }
}
