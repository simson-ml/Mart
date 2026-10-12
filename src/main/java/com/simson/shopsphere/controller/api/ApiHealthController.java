package com.simson.shopsphere.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check API", description = "Endpoints for application liveness and database connectivity verification")
public class ApiHealthController {

    private final DataSource dataSource;

    public ApiHealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    @Operation(summary = "Check application and database health status")
    public ResponseEntity<Map<String, String>> checkHealth() {
        Map<String, String> health = new LinkedHashMap<>();
        health.put("status", "UP");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SELECT 1");
            health.put("db", "UP");
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            health.put("db", "DOWN");
            health.put("status", "DOWN");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }
}
