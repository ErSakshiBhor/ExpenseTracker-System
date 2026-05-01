package com.expense.expense_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * HomeController handles the root URL ("/") and provides basic endpoints
 * for health checks and application status.
 */
@RestController
public class HomeController {

    /**
     * Root endpoint that returns a simple message confirming the backend is running.
     * Access: GET /
     */
    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Expense Tracker Backend is running");
        response.put("status", "OK");
        response.put("timestamp", java.time.Instant.now().toString());
        return response;
    }

    /**
     * Health check endpoint for production deployment and monitoring.
     * Access: GET /health
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "expense-tracker");
        response.put("timestamp", java.time.Instant.now().toString());
        return response;
    }

    /**
     * API info endpoint providing available endpoints.
     * Access: GET /api
     */
    @GetMapping("/api")
    public Map<String, Object> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Expense Tracker API");
        response.put("version", "1.0.0");
        response.put("endpoints", java.util.Arrays.asList(
            "/ - Home",
            "/health - Health Check",
            "/auth/* - Authentication",
            "/api/transactions/* - Transactions"
        ));
        return response;
    }
}