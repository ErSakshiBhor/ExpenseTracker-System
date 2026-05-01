package com.expense.expense_tracker.controller;

import com.expense.expense_tracker.model.User;
import com.expense.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "https://expense-tracker-frontend.vercel.app")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email and password are required.");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User with this email already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User signed up successfully.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
             Map<String, String> response = new HashMap<>();
             response.put("error", "Email and password are required.");
             return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Compare plain text password for basic demonstration
            if (user.getPassword().equals(loginRequest.getPassword())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful.");
                response.put("userId", user.getId());  // NEW: Return user ID
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                return ResponseEntity.ok(response);
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid email or password.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
