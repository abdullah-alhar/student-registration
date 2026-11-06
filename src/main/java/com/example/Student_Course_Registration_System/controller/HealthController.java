package com.example.Student_Course_Registration_System.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check endpoints (no login needed).
 *
 * GET /health     -> {"status":"UP"} as soon as the server is running.
 *                    Used by Render and by the GitHub Pages "waking up" page.
 *                    It does NOT touch the database, so frequent checks don't
 *                    keep Neon awake (Neon sleeps when idle to save free hours).
 * GET /health/db  -> also checks the database. Handy after deploying to see
 *                    whether DATABASE_URL is correct.
 */
@RestController
public class HealthController {

    private final JdbcTemplate jdbc;

    public HealthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/health/db")
    public ResponseEntity<Map<String, String>> databaseHealth() {
        try {
            jdbc.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok(Map.of("status", "UP", "database", "UP"));
        } catch (Exception e) {
            // Details go to the server log only, never to the browser
            System.out.println("Database health check failed: " + e.getMessage());
            return ResponseEntity.status(503).body(Map.of("status", "UP", "database", "DOWN"));
        }
    }
}
